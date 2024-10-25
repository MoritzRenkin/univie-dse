package messages;

import java.util.UUID;


/**
 * Sent out by Vehicle to report its Location to the hub operator.
 */
public class VehiclePositionUpdate extends AbstractMessage {
	private static final long serialVersionUID = 7736794443611909010L;
	
	private final boolean inTravel;
	private final double distanceToGo;
	private final UUID originHub;
	private final UUID targetHub;
	

	public VehiclePositionUpdate(UUID senderUUID, boolean inTravel, double distanceToGo, UUID originHub,
			UUID targetHub) {
		super(senderUUID);
		this.inTravel = inTravel;
		this.distanceToGo = distanceToGo;
		this.originHub = originHub;
		this.targetHub = targetHub;
	}

	public boolean isInTravel() {
		return inTravel;
	}

	public double getDistanceToGo() {
		return distanceToGo;
	}

	public UUID getOriginHub() {
		return originHub;
	}

	public UUID getTargetHub() {
		return targetHub;
	}

	@Override
	public String toString() {
		return "VehiclePositionUpdate{" +
				"messageUUID=" + getMessageUUID() +
				", senderUUID=" + getSenderUUID() +
				", inTravel=" + inTravel +
				", distanceToGo=" + distanceToGo +
				", originHub=" + originHub +
				", targetHub=" + targetHub +
				'}';
	}
}
