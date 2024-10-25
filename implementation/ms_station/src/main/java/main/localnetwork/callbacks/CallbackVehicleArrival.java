package main.localnetwork.callbacks;

import main.MainController;
import main.objects.Container;
import main.objects.ContainerRepository;
import messages.VehicleArrival;
import messages.VehicleOffloadPermission;
import network.Callback;
import network.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CallbackVehicleArrival implements Callback<VehicleArrival> {

    private final Publisher publisher;
    private final ContainerRepository repository;
    private final Logger logger = LoggerFactory.getLogger(CallbackContainerPickUpRequest.class);

    public CallbackVehicleArrival(Publisher publisher) {
        this.publisher = publisher;
        repository = ContainerRepository.getInstance();
    }

    @Override
    public void onResponse(VehicleArrival message) {
        if(message.getTargetHub().equals(MainController.STATION_ID)) {
            logger.info("VehicleOffloadPermission message sent! by" + MainController.STATION_ID);
            publisher.publish(new VehicleOffloadPermission(MainController.STATION_ID, message.getSenderUUID()));
            for (var mes : message.getContainersContained()) {
                repository.addContainerToPickup(new Container(mes.getContainerId(), mes.getDestinationStation(), mes.getWeight(), mes.getCurrentHub()));
            }
            logger.info("New VehicleOffloadPermission published");
        }
    }
}
