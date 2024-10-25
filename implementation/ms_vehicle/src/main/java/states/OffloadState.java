package states;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import data.Container;
import data.Converter;
import messageUtil.ContainerInformation;
import messages.ContainerHandover;
import messages.VehicleArrival;
import messages.VehicleOffloadPermission;
import network.Callback;
import network.GenericDataCalback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OffloadState extends State {
	private static Logger logger = LoggerFactory.getLogger(OffloadState.class); //TODO logging
	private final Collection<Container> containersContained;
	private final UUID sourceNode;
	private final UUID nodeArrived;
	private final int distanceTraveled;

	private final AtomicBoolean offloaded = new AtomicBoolean(true);

	public OffloadState(Collection<Container> containersContained, UUID sourceNode, UUID nodeArrived, int distanceTraveled) {
		this.containersContained = containersContained;
		this.sourceNode = sourceNode;
		this.nodeArrived = nodeArrived;
		this.distanceTraveled = distanceTraveled;

		publishArrival();
		if (containersContained.size() != 0) {
			offloaded.set(false);
			addOffloadPermissionCallback();
		}

	}

	@Override
	public State proceed() {
		if (offloaded.get()) {
			subscriber.removeSubscription(VehicleOffloadPermission.class);

			publishContainerHandovers();

			UUID motherHubId = getVehicleInformation().getMotherHubId();
			if (nodeArrived.equals(motherHubId)) {
				logger.debug("Arrived at MotherHub.");
				return new IdleState(motherHubId);

			} else {
				return getEmptyRetourState();
			}
		} else {
			return this;
		}

	}

	private void publishArrival() {
		UUID selfUUID = getVehicleInformation().getId();

		List<ContainerInformation> convertedContainers = new ArrayList<>();
		for (Container local: containersContained) {
			convertedContainers.add(Converter.getRemoteContainerInformation(local, selfUUID));
		}

		VehicleArrival vehicleArrival = new VehicleArrival(selfUUID, sourceNode, nodeArrived, convertedContainers);
		publisher.publish(vehicleArrival);
	}

	private void addOffloadPermissionCallback() {
		UUID selfUUID = getVehicleInformation().getId();

		Callback<VehicleOffloadPermission> callback = new GenericDataCalback<VehicleOffloadPermission, AtomicBoolean>(offloaded) {
			@Override
			public void onResponse(VehicleOffloadPermission message) {
				if (message.getVehicleId().equals(selfUUID)) {
					if(!message.getSenderUUID().equals(nodeArrived)) {
						logger.warn("Received OffloadPermission from unexpected Node, Ignoring. Message: " + message);
						return;
					}
					data.set(true);
				}
			}
		};
		subscriber.addSubscription(VehicleOffloadPermission.class, callback);
	}

	private JourneyState getEmptyRetourState() {
		JourneyState newState = new JourneyState.Builder()
				.withTotalDistance(distanceTraveled)
				.withSource(nodeArrived)
				.withDestination(sourceNode)
				.withContainersContained(new ArrayList<Container>())
				.setPickup(false)
				.build();
		return newState;
	}

	private void publishContainerHandovers() {
		if (containersContained.size() != 0) {
			logger.info("Publishing " + containersContained.size() + " ContainerHandovers.");
		}
		for (Container container: containersContained) {
			ContainerHandover handover = new ContainerHandover(getVehicleInformation().getId(), nodeArrived, container.getId());
			publisher.publish(handover);
		}
	}

	@Override
	public String toString() {
		return "OffloadState{" +
				"containersContained=" + containersContained +
				", sourceNode=" + sourceNode +
				", nodeArrived=" + nodeArrived +
				", distanceTraveled=" + distanceTraveled +
				", offloaded=" + offloaded +
				'}';
	}
}
