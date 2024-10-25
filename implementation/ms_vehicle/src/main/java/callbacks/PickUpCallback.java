package callbacks;

import data.Container;
import exceptions.UnexpectedContainerException;
import messages.ContainerHandover;
import network.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import states.State;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * This callback assures that all expected Containers are really handed over and none else.
 * handoverComplete is set to true when all Handovers have been received.
 */
public class PickUpCallback implements Callback<ContainerHandover> {
    private static Logger logger = LoggerFactory.getLogger(PickUpCallback.class);
    private final Set<UUID> expectedContainers;
    private final Set<UUID> receivedContainers;
    private final AtomicBoolean handoversComplete;

    public PickUpCallback(Collection<Container> expectedContainers, AtomicBoolean handoversComplete) {
        this.expectedContainers = new HashSet<>();
        for (Container container: expectedContainers) {
            this.expectedContainers.add(container.getId());
        }
        this.handoversComplete = handoversComplete;
        this.receivedContainers = new HashSet<>();

        logger.debug("Waiting for " + expectedContainers.size() + " ContainerHandovers");
    }

    @Override
    public void onResponse(ContainerHandover message) {
        if (message.getTakerUUID().equals(State.getVehicleInformation().getId())) {
            assert (!handoversComplete.get());

            logger.debug("Received ContainerHandover with message: " + message);
            UUID container = message.getContainerUUID();

            if (!expectedContainers.contains(container)) {
                throw new UnexpectedContainerException("Received Container with UUID: " + container +
                        "\n Expected one of: " + expectedContainers);
            }
            if (receivedContainers.contains(container)) {
                logger.warn("Received Container with UUID: " + container + " for the second time. Ignoring second handover.");
                return;
            }

            receivedContainers.add(container);

            if (expectedContainers.size() == receivedContainers.size()) {
                handoversComplete.set(true);
            }

            logger.debug("Waiting for " + (expectedContainers.size() - receivedContainers.size()) + " more ContainerHandovers");
        }
    }
}
