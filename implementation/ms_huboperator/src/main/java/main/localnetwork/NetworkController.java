package main.localnetwork;

import main.MainController;
import main.localnetwork.callbacks.CallbackContainerPositionUpdate;
import main.localnetwork.callbacks.CallbackHubCapacityUpdate;
import main.localnetwork.callbacks.CallbackInstanceOnline;
import main.localnetwork.callbacks.CallbackVehicleOrder;
import main.properties.Properties;
import messages.ContainerPositionUpdate;
import messages.HubCapacityUpdate;
import messages.InstanceOnlineMessage;
import messages.VehicleOrder;
import network.NetworkServiceFactory;
import network.Publisher;
import network.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NetworkController {

    private final Subscriber subscriber;
    private final Publisher publisher;
    private final Logger logger = LoggerFactory.getLogger(NetworkController.class);

    @Autowired
    private CallbackInstanceOnline callbackInstanceOnline;
    @Autowired
    private CallbackContainerPositionUpdate callbackContainerPositionUpdate;
    /*@Autowired
    private CallbackContainerPickUpRequest callbackContainerPickUpRequest;*/
    @Autowired
    private CallbackVehicleOrder callbackVehicleOrder;
    @Autowired
    private CallbackHubCapacityUpdate callbackHubCapacityUpdate;

    public NetworkController() {
        NetworkServiceFactory.initialize(MainController.HUB_OPERATOR_ID);
        subscriber = NetworkServiceFactory.getSubscriber();
        publisher = NetworkServiceFactory.getPublisher();
    }

    public void receiveHubMessages() {
        subscriber.addSubscription(HubCapacityUpdate.class, callbackHubCapacityUpdate);
    }

    public void receiveVehicleMessages() {
        subscriber.addSubscription(VehicleOrder.class, callbackVehicleOrder);
        //subscriber.addSubscription(ContainerPickUpRequest.class, callbackContainerPickUpRequest);
        subscriber.addSubscription(ContainerPositionUpdate.class, callbackContainerPositionUpdate);
    }

    public void receiveInstanceOnlineMessages() {
        subscriber.addSubscription(InstanceOnlineMessage.class, callbackInstanceOnline);
    }

    public void sendStartSignal() {
        var allEntities = Properties.allNonHubOpEntities();
        callbackInstanceOnline.addListener(new InstanceOnlineListener(allEntities, callbackInstanceOnline, publisher));
    }

}
