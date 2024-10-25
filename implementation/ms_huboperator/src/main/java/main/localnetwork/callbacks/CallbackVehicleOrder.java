package main.localnetwork.callbacks;

import main.database.ContainerHistoryState;
import main.database.ContainerState;
import main.database.DatabaseController;
import messageUtil.ContainerInformation;
import messages.VehicleOrder;
import network.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CallbackVehicleOrder implements Callback<VehicleOrder> {

    private final Logger logger = LoggerFactory.getLogger(CallbackVehicleOrder.class);
    @Autowired
    private DatabaseController dbController;

    public CallbackVehicleOrder() {
    }

    @Override
    public void onResponse(VehicleOrder message) {
        logger.info("CallbackVehicleOrder Message received!");
        for (ContainerInformation cont : message.getContainersToCarry()) {
            dbController.addContainer(new ContainerState(cont.getContainerId(), cont.getCurrentHub(), message.getVehicle(), message.getDistance()));
            dbController.addContainerHistoryState(new ContainerHistoryState(cont.getContainerId(), cont.getSourceStation()));
        }
    }
}
