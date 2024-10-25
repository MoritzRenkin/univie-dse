package messages;

import java.util.UUID;

/**
 * Published by Destinations and Hubs after vehicles arrived to signal that there is enough free capacity for the vehicle
 * to handover its containers.
 */
public class VehicleOffloadPermission extends AbstractMessage {

	private static final long serialVersionUID = -3143018912887512142L;
	private final UUID vehicleId;

	public VehicleOffloadPermission(UUID senderUUID, UUID vehicleId) {
		super(senderUUID);
		this.vehicleId = vehicleId;
	}

	public UUID getVehicleId() {
		return vehicleId;
	}

	@Override
	public String toString() {
		return "VehicleOffloadPermission{" +
				"messageUUID=" + getMessageUUID() +
				", senderUUID=" + getSenderUUID() +
				", vehicleId=" + vehicleId +
				'}';
	}
}
