package messages;

import java.util.Collection;
import java.util.UUID;

import messageUtil.ContainerInformation;


/**
 * If a hub sends out containers to another hub/destination node or picks up containers from a source.
 * The Vehicle receives an order with the information what to do.
 */
public class VehicleOrder extends AbstractMessage{
	
	private static final long serialVersionUID = 6598175941522444353L;
	
	private final UUID target;
	private final UUID vehicle;
	private final Collection<ContainerInformation> containersToCarry;
	private final int distance;
	private boolean pickup=false; 
	
	/**
	 * 
	 * @param senderUUID
	 * @param target the UUID of the target node (Hub or Station)
	 * @param vehicle the UUID of the vehicle to do the transport
	 * @param containersToCarry Containers to carry or, if pickup is true: Containers to pickUp at a Source.
	 * @param distance
	 * @param pickup specifies whether the vehicle should pick up a container at a source
	 */
	
	public VehicleOrder(UUID senderUUID, UUID target, UUID vehicle, Collection<ContainerInformation> containersToCarry,
			int distance, boolean pickup) {
		super(senderUUID);
		this.target = target;
		this.vehicle = vehicle;
		this.containersToCarry = containersToCarry;
		this.distance = distance;
		this.pickup = pickup;
	}

	public VehicleOrder(UUID senderUUID, UUID target, UUID vehicle, Collection<ContainerInformation> containersToCarry,
			int distance) {
		super(senderUUID);
		this.target = target;
		this.vehicle = vehicle;
		this.containersToCarry = containersToCarry;
		this.distance = distance;
		this.pickup = false;
	}



	public UUID getTarget() {
		return target;
	}

	public Collection<ContainerInformation> getContainersToCarry() {
		return containersToCarry;
	}

	public int getDistance() {
		return distance;
	}

	public boolean isPickup() {
		return pickup;
	}
	
	
	public UUID getVehicle() {
		return vehicle;
	}

	@Override
	public String toString() {
		return "VehicleOrder{" +
				"messageUUID=" + getMessageUUID() +
				", senderUUID=" + getSenderUUID() +
				", target=" + target +
				", vehicle=" + vehicle +
				", containersToCarry=" + containersToCarry +
				", distance=" + distance +
				", pickup=" + pickup +
				'}';
	}
}
