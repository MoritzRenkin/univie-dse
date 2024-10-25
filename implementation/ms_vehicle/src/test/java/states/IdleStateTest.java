package states;

import data.VehicleInformation;
import data.VehicleType;
import messageUtil.ContainerInformation;
import messages.AbstractMessage;
import messages.VehicleOrder;
import network.Callback;
import network.NetworkServiceFactory;
import network.Publisher;
import network.Subscriber;
import org.junit.BeforeClass;
import org.junit.Test;

import org.junit.*;
import static org.junit.Assert.*;

import java.util.*;


public class IdleStateTest {

    private static final VehicleInformation vehicleInformation = new VehicleInformation(UUID.fromString("38d10f47-8d89-4740-93c8-07408744ad87"), UUID.randomUUID(), VehicleType.CAR);
    private static final UUID vehicleId = vehicleInformation.getId();

    private static final Queue<AbstractMessage> publishQueue = new LinkedList<>();
    private static final Map<Class<? extends AbstractMessage>, Callback<? extends AbstractMessage>> subscriptions = new HashMap<>();

    private static final Subscriber subscriber = new Subscriber(subscriptions);
    private static final Publisher publisher = new Publisher(publishQueue);

    private State state;

    @BeforeClass
    public static void setUp(){
        NetworkServiceFactory.initialize(vehicleId);

        State.setVehicleInformation(vehicleInformation);
        State.subscriber = subscriber;
        State.publisher = publisher;

        NetworkServiceFactory.terminateAllThreads();
    }

    @Before
    public void initState() {
        state = new IdleState(vehicleInformation.getMotherHubId());
    }

    @After
    public void clearQueues() {
        publishQueue.clear();
        subscriptions.clear();
    }

    @Test
    public void newIdleState_addsSubscriptionForVehicleOrder() {

        state = state.proceed();

        assertTrue(state instanceof IdleState);
        assertTrue(subscriber.isSubscriptionPresent(VehicleOrder.class));
    }

    @Test
    public void awaitingVehicleOrder_receivesOrder_returnsJourneyState() {

        assertTrue(subscriber.isSubscriptionPresent(VehicleOrder.class));
        UUID motherHub = vehicleInformation.getMotherHubId();
        UUID destinationHub = UUID.randomUUID();
        Collection<ContainerInformation> containersToCarry = new ArrayList<>();
        containersToCarry.add(new ContainerInformation(UUID.randomUUID(), 20, UUID.randomUUID(), UUID.randomUUID(), motherHub));

        VehicleOrder vehicleOrder = new VehicleOrder(motherHub, destinationHub, vehicleId, containersToCarry, 20);
        Callback<VehicleOrder> callback = (Callback<VehicleOrder>) subscriber.getCallback(VehicleOrder.class).get();
        //calling the callback mimics reception of message
        callback.onResponse(vehicleOrder);

        state = state.proceed();
        System.out.println(state);
        assertTrue(state instanceof JourneyState);
    }
}