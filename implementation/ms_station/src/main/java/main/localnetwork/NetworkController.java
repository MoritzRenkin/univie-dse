package main.localnetwork;

import main.localnetwork.callbacks.CallbackContainerPickUpRequest;
import main.localnetwork.callbacks.CallbackContainerHandover;
import main.localnetwork.callbacks.CallbackVehicleArrival;
import main.objects.ContainerRepository;
import main.MainController;
import main.localnetwork.listener.AddedContainerToSentListener;
import main.localnetwork.listener.ContainerAtFinalDestinationListener;
import messageUtil.EMicroservice;
import messages.ContainerHandover;
import messages.ContainerPickUpRequest;
import messages.InstanceOnlineMessage;
import messages.VehicleArrival;
import network.GenericDataCalback;
import network.NetworkServiceFactory;
import network.Publisher;
import network.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;


@Service
public class NetworkController {

    private final Publisher publisher;
    private final Subscriber subscriber;
    private final ContainerRepository repository;
    private static final Logger logger = LoggerFactory.getLogger(NetworkController.class);

    public NetworkController() {
        NetworkServiceFactory.initialize(MainController.STATION_ID);
        this.publisher = NetworkServiceFactory.getPublisher();
        this.subscriber = NetworkServiceFactory.getSubscriber();
        this.repository = ContainerRepository.getInstance();
    }

    public void publishInstanceOnline() {
        publisher.publish(new InstanceOnlineMessage(MainController.STATION_ID, EMicroservice.STATION));
        AtomicBoolean startSignal = new AtomicBoolean(false);
        var callback = new GenericDataCalback<InstanceOnlineMessage, AtomicBoolean>(startSignal) {

            @Override
            public void onResponse(InstanceOnlineMessage message) {
                if (message.getType() == EMicroservice.HUB_OPERATOR) {
                    this.data.set(true);
                }
            }
        };
        subscriber.addSubscription(InstanceOnlineMessage.class, callback);
        logger.info("Waiting for Start Signal");
        while (!startSignal.get()) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        logger.info("Start Signal received");
        subscriber.removeSubscription(InstanceOnlineMessage.class);
    }

    public void addNewContainerListener() {
        repository.addListener(new AddedContainerToSentListener(publisher));
    }

    public void addContainerOnFinalDestinationListener() {
        repository.addListener(new ContainerAtFinalDestinationListener(publisher));
    }

    public void subscribeToVehicleArrival() {
        subscriber.addSubscription(VehicleArrival.class, new CallbackVehicleArrival(publisher));
    }

    public void subscribeToContainerPickUpRequest() {
        subscriber.addSubscription(ContainerPickUpRequest.class, new CallbackContainerPickUpRequest(publisher));
        logger.info("Subscribed to PickUp Requests");
    }

    public void subscribeToContainerHandover() {
        subscriber.addSubscription(ContainerHandover.class, new CallbackContainerHandover());
        logger.info("Subscribed to Handover Messages");
    }
}
