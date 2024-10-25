package messages;

import java.net.InetAddress;
import java.util.UUID;

/**
 * Only for internal use for Messaging Framework
 * The messaging framework sends out this message if the microservice publishes a container handover.
 * It looks up the IP and port for that neighbour and removes it from the neighbours.
 * The receiver of the message will add the handed over neighbour with the specified ip and port.
 */
public class DynamicNeighbourHandover extends AbstractMessage {
	
	private static final long serialVersionUID = -4364587153060207490L;
	
	private final UUID takerUUID;
	private final UUID neighbourUUID;
	
	private final InetAddress neighbourIp;
	private final int neighbourPort;
	
	public DynamicNeighbourHandover(UUID senderUUID, 
			UUID takerUUID, 
			UUID neighbourUUID, 
			InetAddress neighbourIp,
			int neighbourPort) {
		
		super(senderUUID);
		this.takerUUID = takerUUID;
		this.neighbourUUID = neighbourUUID;
		this.neighbourIp = neighbourIp;
		this.neighbourPort = neighbourPort;
	}

	public UUID getTakerUUID() {
		return takerUUID;
	}

	public UUID getNeighbourUUID() {
		return neighbourUUID;
	}

	public InetAddress getNeighbourIp() {
		return neighbourIp;
	}

	public int getNeighbourPort() {
		return neighbourPort;
	}

	@Override
	public String toString() {
		return "DynamicNeighbourHandover{" +
				"messageUUID=" + getMessageUUID() +
				", senderUUID=" + getSenderUUID() +
				", takerUUID=" + takerUUID +
				", neighbourUUID=" + neighbourUUID +
				", neighbourIp=" + neighbourIp +
				", neighbourPort=" + neighbourPort +
				'}';
	}


}
