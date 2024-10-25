package main.localnetwork.listener;

import main.MainController;
import main.objects.Container;
import main.objects.ContainerRepository;
import messageUtil.ContainerInformation;
import messages.ContainerAtFinalDestination;
import network.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ContainerAtFinalDestinationListener implements PropertyChangeListener {

    private final Publisher publisher;
    private static final Logger logger = LoggerFactory.getLogger(ContainerAtFinalDestinationListener.class);

    public ContainerAtFinalDestinationListener(Publisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if (propertyChangeEvent.getPropertyName().equals(ContainerRepository.CONTAINER_TO_PICKUP)) {
            var newContainer = (Container) propertyChangeEvent.getNewValue();
            if (newContainer.getDestinationLocation().equals(MainController.STATION_ID)) {
                publisher.publish(new ContainerAtFinalDestination(MainController.STATION_ID,
                        new ContainerInformation(newContainer.getId(),
                                newContainer.getWeight(), MainController.STATION_ID,
                                newContainer.getDestinationLocation(), null)));
                logger.info("final destination of Container published: "+ newContainer.getId());
            }
        }
    }
}
