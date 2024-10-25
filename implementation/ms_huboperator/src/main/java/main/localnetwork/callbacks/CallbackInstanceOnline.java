package main.localnetwork.callbacks;

import main.database.DatabaseController;
import main.localnetwork.ObservableQueue;
import messages.InstanceOnlineMessage;
import network.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.beans.PropertyChangeListener;
import java.util.Queue;

@Component
public class CallbackInstanceOnline implements Callback<InstanceOnlineMessage> {

    @Autowired
    private DatabaseController dbController;

    private final ObservableQueue<InstanceOnlineMessage> messagesQueue = new ObservableQueue<>();
    private final Logger logger = LoggerFactory.getLogger(CallbackInstanceOnline.class);

    public void addListener(PropertyChangeListener listener) {
        messagesQueue.addListener(listener);
    }

    @Override
    public void onResponse(InstanceOnlineMessage message) {
        logger.info("Instance Online Message received!" + message.getType().toString());;
        messagesQueue.add(message);
    }

    public Queue<InstanceOnlineMessage> getMessagesQueue() {
        return messagesQueue;
    }
}
