package main;

import exceptions.FullStorageException;
import hub_network.NetworkController;
import jsonConvert.JsonConverter;
import jsonConvert.StartUpInfo;
import map.HubNeighbourInformation;
import map.Location;
import map.MapManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import transport.*;

import java.util.*;

public class MainController {
    private final UUID myUUID;
    private MapManager mapManager;
    private Storage storage;
    private VehicleManager vehicleManager;
    NetworkController networkController;
    private StartUpInfo startUpInfo;

    final int MIN_CONTAINER_STORAGE_TIME = 4000; //for MF container Handover

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    public MainController(UUID myUUID) {
        this.myUUID = myUUID;
    }

    @SuppressWarnings("InfiniteLoopStatement")
    public void startHubService() throws InterruptedException {
        //add infos from config
        initializeHub(myUUID);
        logger.info("hub initialized with info from config.json");

        //set up network
        setUpHub();

        while (true) {

            processPickUpOrders();

            addAllWaitingIdleVehicles();
            processOwnVehicleArrivals();

            sendOutContainers(storage.getPickupOrders(), true);

            processForeignVehicleArrivals();

            addAllWaitingIdleVehicles();
            processOwnVehicleArrivals();

            Thread.sleep(MIN_CONTAINER_STORAGE_TIME);
            sendOutContainers(storage.getStoredContainers(), false);
        }
    }

    /**
     * Processing of pickup orders from sources
     * containers only get processed if they have space (40% of storage reserved)
     */
    public void processPickUpOrders() {
        while (!networkController.getNewContainerAtSourceQueue().isEmpty()) {
            Container container = networkController.getNewContainerAtSourceQueue().peek();
            logger.debug("new container arrived: {}", container.toString());
            Location nextHop = mapManager.getNextHop(container.getSourceStation(), container.getDestination());
            if (nextHop.getID().equals(myUUID)) {
                logger.info("new container at source {} to destination {}", container.getSourceStation().toString(), container.getDestination().toString());
                logger.debug("next hop of container {}", nextHop.toString());
                if (container.getWeight() <= storage.getFreePickUpOrderCapacity()) {
                    try {
                        storage.addPickUpOrder(container);
                        logger.debug("container: {} added to pick up map", container.toString());
                        networkController.getNewContainerAtSourceQueue().remove();
                    } catch (Exception e) { //should not throw
                        logger.error(e.getMessage(), "no storage capacity for pickup-order at the moment");
                    }
                } else {
                    logger.info("Storage has not enough capacity for pick up order");
                    break;
                }
            } else networkController.getNewContainerAtSourceQueue().remove();
        }
    }

    /**
     * this method is for sending out containers, either for pickup or to send out stored containers
     * priority for containers which are longer in storage
     * vehicle must fill MIN_VEHICLE_FILLING (currently 50%) of vehicle, except container in storage for more than MAX_CONTAINER_STORAGE_TIME (currently 30secs)
     */
    public void sendOutContainers(Map<Location, List<Container>> orders, boolean pickup) throws InterruptedException {
        final double MIN_VEHICLE_FILLING = 0.5;
        final int MAX_CONTAINER_STORAGE_TIME = 5000; //5 sec

        while (storage.getPriorityOrder(orders).isPresent()) {
            Location priorityLocation = storage.getPriorityOrder(orders).get();
            int containerWeightSum = orders.get(priorityLocation).stream().mapToInt(Container::getWeight).sum();
            logger.debug("priority location: {}", priorityLocation.toString());

            if (vehicleManager.getFittingVehicle(containerWeightSum).isPresent()) {
                Vehicle fittingVehicle = vehicleManager.getFittingVehicle(containerWeightSum).get();
                logger.debug("fitting vehicle found, id of vehicle: {}", fittingVehicle.getVehicleID());

                if ((orders.get(priorityLocation).get(0).getInStorageTime() > MAX_CONTAINER_STORAGE_TIME)
                        || (containerWeightSum >= (fittingVehicle.getVehicleCapacity() * MIN_VEHICLE_FILLING))) {

                    List<Container> vehicleContainerLoad = new ArrayList<>();
                    logger.info("starting to fill vehicle: {}", fittingVehicle.getVehicleID());

                    while (!orders.get(priorityLocation).isEmpty()) {
                        logger.debug("amount of orders to send in priority location: {}", orders.get(priorityLocation).size());
                        Container nextContainerToLoad = orders.get(priorityLocation).get(0);
                        // logger.debug("next to load container: {}", nextContainerToLoad.toString());
                        if ((vehicleContainerLoad.stream().mapToInt(Container::getWeight).sum() + nextContainerToLoad.getWeight()) <= fittingVehicle.getVehicleCapacity()) {
                            //enough space for container
                            vehicleContainerLoad.add(nextContainerToLoad);
                            logger.debug("loaded container: " + nextContainerToLoad.toString() + "in vehicle: " + fittingVehicle.getVehicleID());
                            try {
                                storage.removeContainer(nextContainerToLoad, pickup);
                            } catch (Exception e) {
                                logger.error("could not remove container", e);
                            }
                        } else if (vehicleContainerLoad.isEmpty()) {
                            logger.info("container too big for vehicle");
                            return;
                        } else {
                            logger.info("vehicle fully loaded");
                            break;
                        }

                    }

                    if (pickup) {
                        fittingVehicle.setVehicleTransportState(EVehicleTransportState.PICKUP);
                    } else {
                        fittingVehicle.setVehicleTransportState(EVehicleTransportState.TRANSPORT);
                        networkController.sendContainerHandover(fittingVehicle, vehicleContainerLoad);
                    }

                    networkController.sendVehicleOrder(priorityLocation, fittingVehicle, vehicleContainerLoad, mapManager.getDistance(priorityLocation));
                    networkController.sendHubCapacityUpdate(storage.getHubCapacity(), storage.getFillingLevel());
                } else {
                    logger.debug("conditions for sending vehicle are not full filled");
                    break;
                }
            } else {
                logger.info("No vehicle available at the moment");
                break;
            }
        }

    }

    /**
     * this methods processes the arrivals of vehicles
     * of own vehicles which bring orders from stations
     */
    public void processOwnVehicleArrivals() {
        while (!networkController.getMyFullVehicleArrivalQueue().isEmpty()) {
            Vehicle nextVehicle = networkController.getMyFullVehicleArrivalQueue().poll();
            logger.info("Vehicle: {} ready to off load", nextVehicle.getVehicleID());
            networkController.sendOffLoadPermission(nextVehicle);
            for (Container container : nextVehicle.getStoredContainers()) {
                container.setFromPickUpOrder(true);
                try {
                    storage.addContainerToStorage(mapManager.getNextHop(container.getDestination()), container);
                    logger.info("Container with UUID: {} added to storage", container.getID());
                } catch (FullStorageException e) { //should not throw
                    logger.error("FullStorageException when tried to add pick up container to storage", e);
                }
                networkController.sendContainerPositionUpdate(container);
            }
            networkController.sendHubCapacityUpdate(storage.getHubCapacity(), storage.getFillingLevel());
            vehicleManager.addVehicleBackToStorage(nextVehicle.getVehicleID());
        }
    }

    /**
     * this methods processes the arrivals of vehicles
     * of vehicles that belong to other hubs
     */
    public void processForeignVehicleArrivals() {
        while (!networkController.getForeignFullVehicleArrivalQueue().isEmpty()) {
            Vehicle nextVehicle = networkController.getForeignFullVehicleArrivalQueue().peek();
            int containerWeightSum = nextVehicle.getStoredContainers().stream()
                    .mapToInt(Container::getWeight)
                    .sum();
            if (containerWeightSum <= storage.getFreeArrivalVehicleSpace()) {
                Vehicle vehicle = networkController.getForeignFullVehicleArrivalQueue().poll();
                logger.info("Vehicle: {} ready to off load", vehicle.getVehicleID());
                networkController.sendOffLoadPermission(vehicle);
                for (Container cont : vehicle.getStoredContainers()) {
                    cont.setFromPickUpOrder(false);
                    try {
                        storage.addContainerToStorage(mapManager.getNextHop(cont.getDestination()), cont);
                        logger.info("Container with UUID: {} added to storage", cont.getID());
                    } catch (FullStorageException e) { //should not throw
                        logger.error("FullStorageException when tried to add pick up container to storage", e);
                    }
                    networkController.sendContainerPositionUpdate(cont);
                }
                networkController.sendHubCapacityUpdate(storage.getHubCapacity(), storage.getFillingLevel());
            } else {
                logger.info("no free capacity in storage for waiting vehicle to off load");
                break;
            }
        }
    }

    public void addAllWaitingIdleVehicles() {
        while (!networkController.getIdleVehicleArrivalQueue().isEmpty()) {
            UUID polledVehicleUUID = networkController.getIdleVehicleArrivalQueue().poll();
            vehicleManager.addVehicleBackToStorage(polledVehicleUUID);
            logger.info("vehicle: {} added back to storage", polledVehicleUUID);
        }
    }

    public void setUpHub() throws InterruptedException {
        networkController.waitForStartSignal();

        // Thread.sleep(8000);
        Queue<HubNeighbourInformation> neighbourInfo = networkController.waitForHubConnectionInformation(startUpInfo.getNeighbourConnections(), startUpInfo.getNumberOfHubs());
        mapManager.addHubNeighbourConnectionsToMap(neighbourInfo);
        logger.info("Neighbour Connections of all hubs added to map");
      /*  for (LocationConnection c : mapManager.getMap().getLocationConnections()) {
            logger.debug("connection in map: " + c.toString());
        }*/
    }

    public void initializeHub(UUID myUUID) {
        JsonConverter jsonConverter = new JsonConverter(myUUID);
        startUpInfo = jsonConverter.convert();

        this.mapManager = new MapManager(myUUID, startUpInfo.getNeighbourConnections());
        this.storage = new Storage(startUpInfo.getHubCapacity());
        this.vehicleManager = new VehicleManager(startUpInfo.getAssignedVehicles());
        this.networkController = new NetworkController(myUUID, startUpInfo.getAssignedVehicles(), startUpInfo.getNumberOfHubs());
    }

    public Storage getStorage() {
        return storage;
    }

    public VehicleManager getVehicleManager() {
        return vehicleManager;
    }

    public NetworkController getNetworkController() {
        return networkController;
    }

    public MapManager getMapManager() {
        return mapManager;
    }
}
