package messages;

import java.util.Collection;
import java.util.UUID;

import messageUtil.ContainerInformation;

/**
 * If the vehicle arrives at the hub/station is has to inform the hub that is here, along with the information about the containers that it carries.
 * Is not sent when vehicles arrive at Stations for a pickup. Refer to ContainerPickUpRequest for more info.
 */
public class VehicleArrival extends AbstractMessage {

	private static final long serialVersionUID = -6700980980856629347L;
	
	private final UUID originHub;
	private final UUID targetHub;
	private final Collection<ContainerInformation> containersContained;
	
	public VehicleArrival(UUID senderUUID, UUID originHub, UUID targetHub,
			Collection<ContainerInformation> containersContained) {
		super(senderUUID);
		this.originHub = originHub;
		this.targetHub = targetHub;
		this.containersContained = containersContained;
	}

	public UUID getOriginHub() {
		return originHub;
	}

	public UUID getTargetHub() {
		return targetHub;
	}

	public Collection<ContainerInformation> getContainersContained() {
		return containersContained;
	}

	@Override
	public String toString() {
		return "VehicleArrival{" +
				"messageUUID=" + getMessageUUID() +
				", senderUUID=" + getSenderUUID() +
				", originHub=" + originHub +
				", targetHub=" + targetHub +
				", containersContained=" + containersContained +
				'}';
	}
}
