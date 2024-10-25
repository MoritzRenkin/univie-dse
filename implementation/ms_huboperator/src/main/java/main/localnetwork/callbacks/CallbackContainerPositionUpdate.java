package main.localnetwork.callbacks;

import main.database.ContainerState;
import main.database.DatabaseController;
import messages.ContainerPositionUpdate;
import network.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CallbackContainerPositionUpdate implements Callback<ContainerPositionUpdate> {

    @Autowired
    private DatabaseController dbController;
    private final Logger logger = LoggerFactory.getLogger(CallbackContainerPositionUpdate.class);

    public CallbackContainerPositionUpdate() {
    }


    @Override
    public void onResponse(ContainerPositionUpdate message) {
        logger.info("ContainerPositionUpdate Message received!");
        var containersOfVehicle = dbController.getContainersOfVehicle(message.getSenderUUID());
        for (ContainerState cont : containersOfVehicle) {
            dbController.addContainer(new ContainerState(cont.getContainerId(), cont.getCurrentLocation(), cont.getVehicleId(), cont.getDistanceToGo()));
        }
    }
}
