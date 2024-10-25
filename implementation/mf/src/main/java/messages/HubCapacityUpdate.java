package messages;

import java.util.UUID;

/**
 * This message contains information about the current capacities of a hub.
 */
public class HubCapacityUpdate extends AbstractMessage {

	private static final long serialVersionUID = -2806639188205846936L;
	
	private final int maxCapacity;
	private final double fillingLevel;
	
	public HubCapacityUpdate(UUID senderUUID, int maxCapacity, double fillingLevel) {
		super(senderUUID);
		this.maxCapacity = maxCapacity;
		this.fillingLevel = fillingLevel;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public int getMaxCapacity() {
		return maxCapacity;
	}

	public double getFillingLevel() {
		return fillingLevel;
	}

	@Override
	public String toString() {
		return "HubCapacityUpdate{" +
				"messageUUID=" + getMessageUUID() +
				", senderUUID=" + getSenderUUID() +
				", maxCapacity=" + maxCapacity +
				", fillingLevel=" + fillingLevel +
				'}';
	}
}
