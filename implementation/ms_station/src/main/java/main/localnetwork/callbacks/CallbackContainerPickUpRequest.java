package main.localnetwork.callbacks;

import main.MainController;
import main.objects.ContainerRepository;
import messages.ContainerHandover;
import messages.ContainerPickUpRequest;
import network.Callback;
import network.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CallbackContainerPickUpRequest implements Callback<ContainerPickUpRequest> {

    private final Publisher publisher;
    private final ContainerRepository repository;
    private final Logger logger = LoggerFactory.getLogger(CallbackContainerPickUpRequest.class);

    public CallbackContainerPickUpRequest(Publisher publisher) {
        this.publisher = publisher;
        repository = ContainerRepository.getInstance();
    }

    @Override
    public void onResponse(ContainerPickUpRequest message) {
        if (message.getStationId().equals(MainController.STATION_ID)) {
            for (var contInf : message.getContainersToHandover()) {
                var result = repository.removeContainerToSent(contInf.getContainerId());
                if(result.isEmpty()) logger.info("No container found with this id "+contInf.getContainerId());
                result.ifPresent(container -> publisher.publish(new ContainerHandover(MainController.STATION_ID, message.getVehicleId(), contInf.getContainerId())));
                //publisher.publish(new ContainerHandover(MainController.STATION_ID, message.getVehicleId(), contInf.getContainerId()));
                logger.info("New Container Handover published");
            }
            logger.info("All container handover messages published for: " + message.getVehicleId().toString());
        }
    }
}
