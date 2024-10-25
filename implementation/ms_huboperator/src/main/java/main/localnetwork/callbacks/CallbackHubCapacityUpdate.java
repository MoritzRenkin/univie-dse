package main.localnetwork.callbacks;

import main.database.DatabaseController;
import main.database.HubState;
import messages.HubCapacityUpdate;
import network.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CallbackHubCapacityUpdate implements Callback<HubCapacityUpdate> {

    @Autowired
    private DatabaseController dbController;
    private final Logger logger = LoggerFactory.getLogger(CallbackHubCapacityUpdate.class);

    public CallbackHubCapacityUpdate() {
    }

    @Override
    public void onResponse(HubCapacityUpdate message) {
        logger.info("HubCapacityUpdate Message received!");
        HubState hubState = new HubState(message.getSenderUUID(), message.getFillingLevel(), message.getMaxCapacity());
        dbController.addHub(hubState);
    }

}
