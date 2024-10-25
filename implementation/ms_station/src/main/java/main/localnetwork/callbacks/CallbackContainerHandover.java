package main.localnetwork.callbacks;

import main.MainController;
import main.objects.ContainerRepository;
import messages.ContainerHandover;
import network.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CallbackContainerHandover implements Callback<ContainerHandover> {

    private final ContainerRepository repository;
    private final Logger logger = LoggerFactory.getLogger(CallbackContainerHandover.class);

    public CallbackContainerHandover() {
        repository = ContainerRepository.getInstance();
    }

    @Override
    public void onResponse(ContainerHandover message) {
        if(message.getTakerUUID().equals(MainController.STATION_ID)){
            //TODO find out how the container is taken from a vehicle
            var container = repository.removeContainerToSent(message.getContainerUUID());
        }
    }
}
