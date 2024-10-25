package integrationTests;

import data.Container;
import data.Converter;
import data.VehicleInformation;
import data.VehicleType;
import main.Controller;
import messageUtil.ContainerInformation;
import messageUtil.EMicroservice;
import messages.*;
import network.Callback;
import network.NetworkServiceFactory;
import network.Publisher;
import network.Subscriber;
import org.apache.commons.math3.analysis.function.Abs;
import org.junit.*;
import states.*;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class PickUpIntegrationTest {

    private Controller vehicleController;
    private Thread vehicleThread;


    private static final UUID vehicleId = UUID.fromString("38d10f47-8d89-4740-93c8-07408744ad87");

    private static final Queue<AbstractMessage> publishQueue = new ConcurrentLinkedQueue<>();

    private static final Subscriber subscriber = new Subscriber(new ConcurrentHashMap<>());
    private static final Publisher publisher = new Publisher(publishQueue);

    private static List<Container> containers;

    private static final int DEFAULT_WAIT_TIME = Controller.getSTATE_REFRESH_TIME() * 2;

    @BeforeClass
    public static void setUp(){

        containers = new ArrayList<>();
        containers.add(new Container(UUID.randomUUID(), 20, UUID.randomUUID(), UUID.randomUUID()));
        containers.add(new Container(UUID.randomUUID(), 20, UUID.randomUUID(), UUID.randomUUID()));
    }


    @Test
    public void journeyToStationPickUpAndBack() throws InterruptedException {

        vehicleController = new Controller(vehicleId);
        vehicleThread = new Thread(() -> {
            try {
                vehicleController.startMicroService();
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });
        vehicleThread.setDaemon(true);
        vehicleThread.start();
        Thread.sleep(DEFAULT_WAIT_TIME* 2L);
        setPubSub();
        VehicleInformation vehicleInformation = State.getVehicleInformation();
        UUID destination = UUID.randomUUID();
        final int distance = 2;

        sendStartSignal();
        Thread.sleep(DEFAULT_WAIT_TIME);
        assertTrue(subscriber.isSubscriptionPresent(VehicleOrder.class));

        invokeCallback(new VehicleOrder(vehicleInformation.getMotherHubId(), destination, vehicleInformation.getId(), Converter.getAllRemoteContainerInformation(containers, vehicleInformation.getId()), distance, true));
        Thread.sleep(DEFAULT_WAIT_TIME);
        assertTrue(vehicleController.getCurrState() instanceof JourneyState);

        while(vehicleController.getCurrState() instanceof JourneyState) {
            Thread.sleep(50);
        }
        assertTrue(vehicleController.getCurrState() instanceof PickUpState);

        //Test message exchange at pickup
        int vehicleArrivalAmount = 0;
        List<ContainerPickUpRequest> pickUpRequests = new ArrayList<>();
        for(AbstractMessage eachMessage: publishQueue) {
            if (eachMessage instanceof VehicleArrival) {
                ++vehicleArrivalAmount;
            } else if (eachMessage instanceof ContainerPickUpRequest) {
                pickUpRequests.add((ContainerPickUpRequest) eachMessage);
            }
        }
        publishQueue.clear();
        assertEquals(0, vehicleArrivalAmount);
        assertEquals(1, pickUpRequests.size());

        ContainerPickUpRequest pickUpRequest = pickUpRequests.get(0);
        assertEquals(containers.size(), pickUpRequest.getContainersToHandover().size());
        assertTrue(pickUpRequest.getContainersToHandover().containsAll(Converter.getAllRemoteContainerInformation(containers, vehicleId)));

        for(ContainerInformation containerInformation: pickUpRequest.getContainersToHandover())  {
            Thread.sleep(DEFAULT_WAIT_TIME);
            assertTrue(vehicleController.getCurrState() instanceof PickUpState);
            invokeCallback(new ContainerHandover(destination, vehicleId, containerInformation.getContainerId()));
        }
        Thread.sleep(DEFAULT_WAIT_TIME);

        assertTrue(vehicleController.getCurrState() instanceof JourneyState);

        while(vehicleController.getCurrState() instanceof JourneyState) {
            Thread.sleep(50);
        }
        Thread.sleep(DEFAULT_WAIT_TIME);

        assertTrue(vehicleController.getCurrState() instanceof OffloadState);

        List<VehicleArrival> vehicleArrivals = publishQueue
                .stream()
                .filter(msg -> msg instanceof VehicleArrival)
                .map(msg -> (VehicleArrival) msg)
                .collect(Collectors.toList());
        assertEquals(1, vehicleArrivals.size());

        invokeCallback(new VehicleOffloadPermission(vehicleInformation.getMotherHubId(), vehicleId));
        Thread.sleep(DEFAULT_WAIT_TIME);
        assertTrue(vehicleController.getCurrState() instanceof IdleState);

        List<ContainerHandover> containerHandovers = publishQueue
                .stream()
                .filter(msg -> msg instanceof ContainerHandover)
                .map(msg -> (ContainerHandover) msg)
                .collect(Collectors.toList());
        assertEquals(containers.size(), containerHandovers.size());


        for (ContainerHandover containerHandover: containerHandovers) {
            System.out.println(containerHandover);
            assertEquals(vehicleId, containerHandover.getSenderUUID());
            assertEquals(vehicleInformation.getMotherHubId(), containerHandover.getTakerUUID());
        }
    }

    private void sendStartSignal() {
        try {
            Socket socket = new Socket(InetAddress.getLocalHost(), 9001);
            ObjectOutputStream objOut = new ObjectOutputStream(socket.getOutputStream());

            objOut.writeObject(new InstanceOnlineMessage(UUID.randomUUID(), EMicroservice.HUB_OPERATOR));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setPubSub() {
        State dummyState = new State() {
            @Override
            public State proceed() {
                State.subscriber = PickUpIntegrationTest.subscriber;
                State.publisher = PickUpIntegrationTest.publisher;
                return null;
            }
            @Override
            public String toString() {
                return null;
            }
        };
        dummyState.proceed();
    }

    private <T extends AbstractMessage> void invokeCallback(T message) {
        assertTrue(subscriber.isSubscriptionPresent(message.getClass()));
        Callback callback = subscriber.getCallback(message.getClass()).get();
        callback.onResponse(message);
    }
}
