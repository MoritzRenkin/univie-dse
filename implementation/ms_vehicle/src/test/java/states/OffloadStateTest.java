package states;

import data.Container;
import data.VehicleInformation;
import data.VehicleType;
import messages.AbstractMessage;
import messages.ContainerHandover;
import messages.VehicleArrival;
import messages.VehicleOffloadPermission;
import network.Callback;
import network.NetworkServiceFactory;
import network.Publisher;
import network.Subscriber;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class OffloadStateTest {

    private static final VehicleInformation vehicleInformation = new VehicleInformation(UUID.fromString("38d10f47-8d89-4740-93c8-07408744ad87"), UUID.randomUUID(), VehicleType.CAR);
    private static final UUID vehicleId = vehicleInformation.getId();

    private static final Queue<AbstractMessage> publishQueue = new LinkedList<>();
    private static final Map<Class<? extends AbstractMessage>, Callback<? extends AbstractMessage>> subscriptions = new HashMap<>();

    private static final Subscriber subscriber = new Subscriber(subscriptions);
    private static final Publisher publisher = new Publisher(publishQueue);

    private static List<Container> defaultContainers;

    @BeforeClass
    public static void setUp(){
        NetworkServiceFactory.initialize(vehicleId);

        State.setVehicleInformation(vehicleInformation);
        State.subscriber = subscriber;
        State.publisher = publisher;

        NetworkServiceFactory.terminateAllThreads();

        defaultContainers = new ArrayList<>();
        defaultContainers.add(new Container(UUID.randomUUID(), 20, UUID.randomUUID(), UUID.randomUUID()));
        defaultContainers.add(new Container(UUID.randomUUID(), 20, UUID.randomUUID(), UUID.randomUUID()));
    }

    @After
    public void clearQueues() {
        publishQueue.clear();
        subscriptions.clear();
    }

    @Test
    public void delivery_arrivesAtHub_sendsVehicleArrival() {
        UUID destination = UUID.randomUUID();

        State state = getDeliveryArrivalState(destination);

        List<VehicleArrival> vehicleArrivals = publishQueue
                .stream()
                .filter(msg -> msg instanceof VehicleArrival)
                .distinct()
                .map(msg -> (VehicleArrival) msg)
                .collect(Collectors.toList());

        assertEquals(1, vehicleArrivals.size());

        VehicleArrival vehicleArrival = vehicleArrivals.get(0);
        assertEquals(vehicleInformation.getMotherHubId(), vehicleArrival.getOriginHub());
        assertEquals(destination, vehicleArrival.getTargetHub());

        assertEquals(defaultContainers.size(), vehicleArrival.getContainersContained().size());
    }

    @Test
    public void arrivedAtHub_receiveOffloadPermission_sendsContainerHandovers() {
        UUID destination = UUID.randomUUID();

        State state = getDeliveryArrivalState(destination);

        // Receive OffloadPermission
        assertTrue(subscriber.isSubscriptionPresent(VehicleOffloadPermission.class));
        Callback callback = subscriber.getCallback(VehicleOffloadPermission.class).get();
        callback.onResponse(new VehicleOffloadPermission(destination, vehicleId));
        state.proceed();

        List<ContainerHandover> handovers = publishQueue
                .stream()
                .filter(msg -> msg instanceof ContainerHandover)
                .map(msg -> (ContainerHandover) msg)
                .collect(Collectors.toList());

        assertEquals(defaultContainers.size(), handovers.size());

        Set<UUID> containerUUIDs =  defaultContainers
                .stream()
                .map(container -> (UUID) container.getId())
                .collect(Collectors.toSet());

        for(ContainerHandover handover: handovers) {
            assertTrue(containerUUIDs.contains(handover.getContainerUUID()));
            assertEquals(vehicleId, handover.getSenderUUID());
            assertEquals(destination, handover.getTakerUUID());
        }
    }

    @Test
    public void arrivedAtHub_receiveOffloadPermission_returnsJourneyState() {
        UUID destination = UUID.randomUUID();

        State state = getDeliveryArrivalState(destination);

        // Receive OffloadPermission
        assertTrue(subscriber.isSubscriptionPresent(VehicleOffloadPermission.class));
        Callback callback = subscriber.getCallback(VehicleOffloadPermission.class).get();
        callback.onResponse(new VehicleOffloadPermission(destination, vehicleId));
        state = state.proceed();

        assertTrue(state instanceof JourneyState);
    }

    private State getDeliveryArrivalState(UUID destination) {
        State state = new JourneyState.Builder()
                .withSource(vehicleInformation.getMotherHubId())
                .withDestination(destination)
                .withTotalDistance(1)
                .withContainersContained(defaultContainers)
                .setPickup(false)
                .build();

        while(state instanceof JourneyState) {
            state = state.proceed();
        }
        assertTrue(state instanceof OffloadState);
        return state;
    }

}