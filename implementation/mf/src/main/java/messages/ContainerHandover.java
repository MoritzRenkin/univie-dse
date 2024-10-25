package messages;

import java.util.UUID;

/**
 * This message must be sent out by MS handing over a Container to another MS.
 * Even if this message is not subscribed by any other service, it is processed by the Messaging Framework.
 */
public class ContainerHandover extends AbstractMessage{

	private static final long serialVersionUID = 8786043503070412257L;
	
	private final UUID takerUUID;
	private final UUID containerUUID;

	
	public ContainerHandover(UUID senderUUID, UUID takerUUID, UUID containerUUID) {
		super(senderUUID);
		this.takerUUID = takerUUID;
		this.containerUUID = containerUUID;
	}


	public UUID getTakerUUID() {
		return takerUUID;
	}


	public UUID getContainerUUID() {
		return containerUUID;
	}


	@Override
	public String toString() {
		return "ContainerHandover{" +
				"messageUUID=" + getMessageUUID() +
				", senderUUID=" + getSenderUUID() +
				", takerUUID=" + takerUUID +
				", containerUUID=" + containerUUID +
				'}';
	}
}
