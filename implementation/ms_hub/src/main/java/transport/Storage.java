package transport;

import exceptions.FullStorageException;
import map.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Storage {
    private static final Logger logger = LoggerFactory.getLogger(Storage.class);
    private final int hubCapacity;
    private int occupiedCapacity;
    private double freeArrivalVehicleSpace;
    private final Map<Location, List<Container>> storedContainers; //sorted by next hop
    private double freePickUpOrderSpace;

    static final double MAX_PICKUP_ORDER_CAPACITY = 0.4; // percentage of storage reserved for pickups
    static final double MAX_ARRIVING_CONTAINER_CAPACITY = 0.6;
    //only virtual in storage
    private final Map<Location, List<Container>> pickupOrders; //sorted by sources

    public Storage(int hubCapacity) {
        this.hubCapacity = hubCapacity;
        this.occupiedCapacity = 0;
        this.storedContainers = new HashMap<>();
        this.pickupOrders = new HashMap<>();
        this.freePickUpOrderSpace = hubCapacity * MAX_PICKUP_ORDER_CAPACITY;
        this.freeArrivalVehicleSpace = hubCapacity * MAX_ARRIVING_CONTAINER_CAPACITY;
    }

    public void removeContainer(Container containerToRemove, boolean inPickUpMap) {
        if (containerToRemove.isFromPickUpOrder()) {
            if (inPickUpMap) {
                pickupOrders.get(findLocation(pickupOrders, containerToRemove)).remove(containerToRemove);
            } else {
                storedContainers.get(findLocation(storedContainers, containerToRemove)).remove(containerToRemove);
                occupiedCapacity = occupiedCapacity - containerToRemove.getWeight();
                freePickUpOrderSpace += containerToRemove.getWeight();
                logger.debug("updated value of occupiedCapacity: {}", occupiedCapacity);
                logger.debug("updated value of freePickUpOrderSpace: {}", freePickUpOrderSpace);
            }
        } else {
            storedContainers.get(findLocation(storedContainers, containerToRemove)).remove(containerToRemove);
            freeArrivalVehicleSpace += containerToRemove.getWeight();
            occupiedCapacity = occupiedCapacity - containerToRemove.getWeight();
            logger.debug("updated value of occupiedCapacity: {}", occupiedCapacity);
            logger.debug("updated value of freeArrivalVehicleSpace: {}", freeArrivalVehicleSpace);
        }

    }

    private Location findLocation(Map<Location, List<Container>> containers, Container containerToRemove) {
        for (Location location : containers.keySet()) {
            for (Container cont : containers.get(location)) {
                if (cont.equals(containerToRemove)) {
                    // toRemoveIndex = containers.get(location).indexOf(cont);
                    return location;
                }
            }
        }

        logger.debug("could not remove container: {}", containerToRemove.getID());
        throw new IllegalArgumentException("container does not exist and cannot be removed");
    }

    public void addPickUpOrder(Container containerOrder) {
        if (freePickUpOrderSpace < containerOrder.getWeight()) {
            throw new FullStorageException("No free storage capacity for this pickup-order!");
        } else {
            containerOrder.setFromPickUpOrder(true);
            containerOrder.resetStorageTime();
            freePickUpOrderSpace = freePickUpOrderSpace - (double) containerOrder.getWeight(); //update space
            logger.debug("updated value of freePickUpOrderSpace: {}", freePickUpOrderSpace);
            if (pickupOrders.containsKey(containerOrder.getSourceStation())) {
                pickupOrders.get(containerOrder.getSourceStation()).add(containerOrder);
            } else {
                List<Container> containers = new ArrayList<>();
                containers.add(containerOrder);
                pickupOrders.put(containerOrder.getSourceStation(), containers);
            }
        }
    }

    public void addContainerToStorage(Location nextHop, Container container) {
        if ((hubCapacity-occupiedCapacity)<container.getWeight()) {
            throw new FullStorageException("no storage capacity to add a new container");
        }
        container.resetStorageTime();
        if (storedContainers.containsKey(nextHop)) {
            storedContainers.get(nextHop).add(container);
        } else {
            List<Container> containers = new ArrayList<>();
            containers.add(container);
            storedContainers.put(nextHop, containers);
        }
        //update storage
        if (!container.isFromPickUpOrder()) { //only for foreign arrivals, pickups already considered
            freeArrivalVehicleSpace = freeArrivalVehicleSpace - container.getWeight();
            logger.debug("updated value of freeArrivalVehicleSpace: {}", freeArrivalVehicleSpace);
        }
        occupiedCapacity = occupiedCapacity + container.getWeight();
        logger.debug("updated value of occupiedCapacity: {}", occupiedCapacity);
    }

    /**
     * used for sending out containers to other hubs, and pick up orders from stations to get the priority order
     * returns order with longest time in the given map
     */
    public Optional<Location> getPriorityOrder(Map<Location,List<Container>> orders){
        if (orders.isEmpty()) {
            return Optional.empty();
        }

        Location minLocation = null;
        long minStoredTime = 0;
        boolean first = true;
        for (Location location : orders.keySet()) {
            if (!orders.get(location).isEmpty()) {
                if (first) {
                    minLocation = location;
                    minStoredTime = orders.get(location).get(0).getAddedToStorageTime();
                    first = false;
                } else {
                    if (orders.get(location).get(0).getAddedToStorageTime() < minStoredTime) {
                        minLocation = location;
                        minStoredTime = orders.get(location).get(0).getAddedToStorageTime();
                    }
                }
            }
        }
        if (minLocation != null) {
            return Optional.of(minLocation);
        } else return Optional.empty();

    }

    public double getFillingLevel() {
        return ((double)occupiedCapacity)/((double)hubCapacity);
    }

    //GETTER
    public int getOccupiedCapacity() {
        return occupiedCapacity;
    }

    public double getFreePickUpOrderCapacity() {
        return freePickUpOrderSpace;
    }

    public double getFreeArrivalVehicleSpace() {
        return freeArrivalVehicleSpace;
    }

    public int getHubCapacity() {
        return hubCapacity;
    }

    public Map<Location, List<Container>> getPickupOrders() {
        return pickupOrders;
    }

    public Map<Location, List<Container>> getStoredContainers() {
        return storedContainers;
    }

}
