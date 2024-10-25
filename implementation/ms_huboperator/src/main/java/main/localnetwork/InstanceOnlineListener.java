package main.localnetwork;

import main.MainController;
import main.localnetwork.callbacks.CallbackInstanceOnline;
import messageUtil.EMicroservice;
import messages.AbstractMessage;
import messages.InstanceOnlineMessage;
import network.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;


public class InstanceOnlineListener implements PropertyChangeListener {

    private final List<UUID> allEntities;
    private final CallbackInstanceOnline callbackInstanceOnline;
    private final Logger logger = LoggerFactory.getLogger(InstanceOnlineListener.class);
    private final Publisher publisher;


    public InstanceOnlineListener(List<UUID> allEntities, CallbackInstanceOnline callbackInstanceOnline, Publisher publisher) {
        this.allEntities = allEntities;
        this.callbackInstanceOnline = callbackInstanceOnline;
        this.publisher = publisher;
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        logger.info("Added new Entity!");
        var messages = callbackInstanceOnline.getMessagesQueue();

        var currentlyOnline = messages.stream().map(AbstractMessage::getSenderUUID).collect(toList());
        if (currentlyOnline.containsAll(allEntities)) {
            publisher.publish(new InstanceOnlineMessage(MainController.HUB_OPERATOR_ID, EMicroservice.HUB_OPERATOR));
            logger.info("Sent start signal");
        } else {
            allEntities.stream()
                    .filter(element -> !currentlyOnline.contains(element))
                    .forEach(element -> logger.info("Entity: " + element.toString() + "is not yet online!"));
        }
    }
}
