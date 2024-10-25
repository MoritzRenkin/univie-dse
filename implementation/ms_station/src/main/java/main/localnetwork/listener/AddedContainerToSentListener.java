package main.localnetwork.listener;

import main.MainController;
import main.objects.Container;
import main.objects.ContainerRepository;
import messageUtil.ContainerInformation;
import messages.NewContainerAtSource;
import network.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.InetAddress;
import java.net.UnknownHostException;


public class AddedContainerToSentListener implements PropertyChangeListener {

    private final Publisher publisher;
    private static final Logger logger = LoggerFactory.getLogger(AddedContainerToSentListener.class);

    public AddedContainerToSentListener(Publisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if (propertyChangeEvent.getPropertyName().equals(ContainerRepository.CONTAINER_TO_SEND)) {
            try {
                var newContainer = (Container) propertyChangeEvent.getNewValue();
                publisher.publish(new NewContainerAtSource(MainController.STATION_ID,
                        new ContainerInformation(newContainer.getId(), newContainer.getWeight(),
                                MainController.STATION_ID, newContainer.getDestinationLocation(), null),
                        InetAddress.getByName(MainController.STATION.getIP()), newContainer.getPort()));
                logger.info("New Container at:" + MainController.STATION_ID + "was published");
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
    }
}
