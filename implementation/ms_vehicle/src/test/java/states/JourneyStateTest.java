package states;

import data.Container;
import data.VehicleInformation;
import data.VehicleType;
import messageUtil.ContainerInformation;
import messages.*;
import network.Callback;
import network.NetworkServiceFactory;
import network.Publisher;
import network.Subscriber;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * This class not only tests the JourneyState itself but also partially OffloadState and PickUpState as they usually follow a JourneyState on arrival
 */
public class JourneyStateTest {

    private static final VehicleInformation vehicleInformation = new VehicleInformation(UUID.fromString("38d10f47-8d89-4740-93c8-07408744ad87"), UUID.randomUUID(), VehicleType.CAR);
    private static final UUID vehicleId = vehicleInformation.getId();

    private static final Queue<AbstractMessage> publishQueue = new LinkedList<>();
    private static final Map<Class<? extends AbstractMessage>, Callback<? extends AbstractMessage>> subscriptions = new HashMap<>();

    private static final Subscriber subscriber = new Subscriber(subscriptions);
    private static final Publisher publisher = new Publisher(publishQueue);

    @BeforeClass
    public static void setUp(){
        NetworkServiceFactory.initialize(vehicleId);

        State.setVehicleInformation(vehicleInformation);
        State.subscriber = subscriber;
        State.publisher = publisher;

        NetworkServiceFactory.terminateAllThreads();
    }

    @After
    public void clearQueues() {
        publishQueue.clear();
        subscriptions.clear();
    }

    @Test
    public void deliveryOngoing_vehiclePositionUpdateSent() {
        State state = JourneyState.Builder()
                .withSource(vehicleInformation.getMotherHubId())
                .withDestination(UUID.randomUUID())
                .withTotalDistance(9999999)
                .withContainersContained(new ArrayList<>())
                .build();

        state.proceed();

        assertEquals(1, publishQueue.size());
        assertTrue(publishQueue.poll() instanceof VehiclePositionUpdate);
    }

    @Test
    public void deliveryOngoing_sendsAllContainerPositionUpdates() {
        Collection<Container> containers = getContainers();

        State state = new JourneyState.Builder()
                .withSource(vehicleInformation.getMotherHubId())
                .withDestination(UUID.randomUUID())
                .withTotalDistance(999999)
                .withContainersContained(new ArrayList<>(containers))
                .build();

        assertEquals(0, publishQueue.size());
        state.proceed();


        List<AbstractMessage> containerPosUpdates = publishQueue.stream()
                .filter(msg -> msg instanceof ContainerPositionUpdate)
                .collect(Collectors.toList());

        assertEquals(containers.size(), (containerPosUpdates.size()));
    }

    @Test
    public void delivery_arrivesAtHub_returnsOffloadState() {
        UUID destination = UUID.randomUUID();

        State state = new JourneyState.Builder()
                .withSource(vehicleInformation.getMotherHubId())
                .withDestination(destination)
                .withTotalDistance(1)
                .withContainersContained(getContainers())
                .setPickup(false)
                .build();

        while(state instanceof JourneyState) {
            state = state.proceed();
        }

        assertTrue(state instanceof OffloadState);
    }



    public Collection<Container> getContainers() {
        Collection<Container> containers = new ArrayList<>();
        containers.add(new Container(UUID.randomUUID(), 20, UUID.randomUUID(), UUID.randomUUID()));
        containers.add(new Container(UUID.randomUUID(), 20, UUID.randomUUID(), UUID.randomUUID()));
        return containers;
    }

}