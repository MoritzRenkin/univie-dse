package hub_network;

import map.HubNeighbourInformation;
import map.Location;
import map.LocationConnection;
import map.MS_Type;
import messageUtil.ContainerInformation;
import messageUtil.EMicroservice;
import messageUtil.NodeConnection;
import messages.*;
import network.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import transport.Container;
import transport.EVehicleTransportState;
import transport.Vehicle;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class NetworkController {
    private static final Logger logger = LoggerFactory.getLogger(NetworkController.class);
    private final UUID myUUID;
    private final Publisher publisher;
    private final Subscriber subscriber;

    //callback data
    Queue<Container> newContainerAtSourceQueue = new ConcurrentLinkedQueue<>();

    Queue<UUID> idleVehicleArrivalQueue = new ConcurrentLinkedQueue<>();
    Queue<Vehicle> myFullVehicleArrivalQueue = new ConcurrentLinkedQueue<>();
    Queue<Vehicle> foreignFullVehicleArrivalQueue = new ConcurrentLinkedQueue<>();

    Queue<HubNeighbourInformation> hubConnectionInfo = new ConcurrentLinkedQueue<>();

    //callback classes
    Callback<NewContainerAtSource> newContainerAtSourceCallback;
    Callback<VehicleArrival> vehicleArrivalCallback;
    Callback<HubConnectionInformation> hubConnectionInformationCallback;

    public NetworkController(UUID myUUID, Set<Vehicle> assignedVehicles, int numberOfHubs) {
        NetworkServiceFactory.initialize(myUUID);
        this.myUUID = myUUID;
        this.publisher = NetworkServiceFactory.getPublisher();
        this.subscriber = NetworkServiceFactory.getSubscriber();
        setUpCallbackClasses(assignedVehicles, new AtomicInteger(numberOfHubs));
    }

    private void setUpCallbackClasses(Set<Vehicle> assignedVehicles, AtomicInteger numberOfHubs) {
        this.newContainerAtSourceCallback = new CallbackNewContainerAtSource(newContainerAtSourceQueue);
        subscriber.addSubscription(NewContainerAtSource.class, newContainerAtSourceCallback);
        this.vehicleArrivalCallback = new CallbackVehicleArrival(myUUID, assignedVehicles, idleVehicleArrivalQueue, myFullVehicleArrivalQueue, foreignFullVehicleArrivalQueue);
        subscriber.addSubscription(VehicleArrival.class, vehicleArrivalCallback);
        this.hubConnectionInformationCallback = new CallbackHubConnectionInformation(hubConnectionInfo, numberOfHubs);
        subscriber.addSubscription(HubConnectionInformation.class, hubConnectionInformationCallback);
    }

    public void waitForStartSignal() throws InterruptedException {
        publisher.publish(new InstanceOnlineMessage(myUUID, EMicroservice.HUB));
        logger.info("InstanceOnlineMessage sent");
        AtomicBoolean startSignal = new AtomicBoolean(false);
        Callback<InstanceOnlineMessage> callback = new GenericDataCalback<>(startSignal) {

            @Override
            public void onResponse(InstanceOnlineMessage message) {
                if (message.getType().equals(EMicroservice.HUB_OPERATOR)) {
                    this.data.set(true);
                    logger.info("start signal from hub_operator");
                }
            }
        };
        subscriber.addSubscription(InstanceOnlineMessage.class, callback);
        while (!startSignal.get()) {
            Thread.sleep(50);
        }
        subscriber.removeSubscription(InstanceOnlineMessage.class);

    }

    public Queue<HubNeighbourInformation> waitForHubConnectionInformation(Set<LocationConnection> myNeighbourConnections, int numberOfHubs) throws InterruptedException {
        Collection<NodeConnection> connections = new HashSet<>();
        for (LocationConnection locationConnection : myNeighbourConnections) {
            for (Location location : locationConnection.getPathEnds()) {
                if (!location.getID().equals(myUUID)) {
                    if (location.getType().equals(MS_Type.HUB)) {
                        connections.add(new NodeConnection(true, locationConnection.getDistance(), location.getID()));
                    } else if (location.getType().equals(MS_Type.STATION)) {
                        connections.add(new NodeConnection(false, locationConnection.getDistance(), location.getID()));
                    }
                }
            }
        }
        publisher.publish(new HubConnectionInformation(myUUID, connections, myUUID));
        logger.info("HubConnectionInformation sent");

        logger.info("waiting for HubConnectionInformation from neighbours");
        while (hubConnectionInfo.size() < numberOfHubs - 1) {
            Thread.sleep(50);
        }
        subscriber.removeSubscription(HubConnectionInformation.class);
        return hubConnectionInfo;
    }

    public void sendOffLoadPermission(Vehicle vehicleToOffLoad) {
        publisher.publish(new VehicleOffloadPermission(myUUID, vehicleToOffLoad.getVehicleID()));
        logger.info("Off Load Permission sent to vehicle: {}", vehicleToOffLoad.getVehicleID());
    }

    public void sendHubCapacityUpdate(int maxCapacity, double fillingLevel) {
        publisher.publish(new HubCapacityUpdate(myUUID, maxCapacity, fillingLevel));
        logger.info("Hub Capacity Update sent");
    }

    public void sendVehicleOrder(Location destination, Vehicle vehicle, Collection<Container> containers, int distance) {
        Collection<ContainerInformation> contInfos = new HashSet<>();
        for (Container cont : containers) {
            contInfos.add(new ContainerInformation(cont.getID(), cont.getWeight(), cont.getSourceStation().getID(),
                    cont.getDestination().getID(), myUUID));
        }
        AtomicBoolean pickup = new AtomicBoolean(false);
        if (vehicle.getVehicleTransportState().equals(EVehicleTransportState.PICKUP)) {
            pickup.set(true);
        }
        publisher.publish(new VehicleOrder(myUUID, destination.getID(), vehicle.getVehicleID(), contInfos, distance, pickup.get()));
        logger.info("Vehicle order ({}) to vehicle: {} to destination: {} sent", vehicle.getVehicleTransportState(), vehicle.getVehicleID(), destination.getID());
    }

    public void sendContainerPositionUpdate(Container cont) {
        //after vehicle arrives with container and gets to offload
        publisher.publish(new ContainerPositionUpdate(myUUID, new ContainerInformation(cont.getID(), cont.getWeight(), cont.getSourceStation().getID(),
                cont.getDestination().getID(), myUUID)));
        logger.info("Container Position Update of Container {} sent", cont.getID());
    }

    public void sendContainerHandover(Vehicle taker, Collection<Container> containers) {
        //send when giving container to vehicle
        for (Container cont : containers) {
            publisher.publish(new ContainerHandover(myUUID, taker.getVehicleID(), cont.getID()));
        }
        logger.info("Container Handover to vehicle taker: {} sent", taker.getVehicleID());
    }

    //getter
    public Queue<Container> getNewContainerAtSourceQueue() {
        return newContainerAtSourceQueue;
    }

    public Queue<UUID> getIdleVehicleArrivalQueue() {
        return idleVehicleArrivalQueue;
    }

    public Queue<Vehicle> getMyFullVehicleArrivalQueue() {
        return myFullVehicleArrivalQueue;
    }

    public Queue<Vehicle> getForeignFullVehicleArrivalQueue() {
        return foreignFullVehicleArrivalQueue;
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public Subscriber getSubscriber() {
        return subscriber;
    }
}
