package states;

import data.Container;
import data.VehicleInformation;
import data.VehicleType;
import messages.AbstractMessage;
import messages.ContainerPickUpRequest;
import messages.VehicleArrival;
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

public class PickUpStateTest {

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
    public void pickupJourneyOngoing_arrivesAtStation_sendsPickUpRequest() {
        UUID destination = UUID.randomUUID();

        State state = getPickupArrivalState(destination);

        List<VehicleArrival> vehicleArrivals = new ArrayList<>();
        List<ContainerPickUpRequest> pickUpRequests = new ArrayList<>();

        for (AbstractMessage msg: publishQueue) {
            if(msg instanceof VehicleArrival) {
                vehicleArrivals.add((VehicleArrival) msg);
            } else if (msg instanceof ContainerPickUpRequest) {
                pickUpRequests.add((ContainerPickUpRequest) msg);
            }
        }
        assertEquals(0, vehicleArrivals.size());
        assertEquals(1, pickUpRequests.size());

        ContainerPickUpRequest pickUpRequest = pickUpRequests.get(0);

        assertEquals(defaultContainers.size(), pickUpRequest.getContainersToHandover().size());
    }

    private State getPickupArrivalState(UUID destination) {
        State state = new JourneyState.Builder()
                .withSource(vehicleInformation.getMotherHubId())
                .withDestination(destination)
                .withTotalDistance(1)
                .withContainersContained(defaultContainers)
                .setPickup(true)
                .build();

        while(state instanceof JourneyState) {
            state = state.proceed();
        }
        assertTrue(state instanceof PickUpState);
        return state;
    }

}