package messages;

import java.util.UUID;

import messageUtil.EMicroservice;

/**
 * This message has to be sent by all microservices when they boot up. 
 * However, they shall not start operation until the Hub Operator sends its InstanceOnlineMessage
 *
 */
public class InstanceOnlineMessage extends AbstractMessage{

	private static final long serialVersionUID = 3066402615592520848L;
	private final EMicroservice type;
	
	public InstanceOnlineMessage(UUID senderUUID, EMicroservice type) {
		super(senderUUID);
		this.type = type;
	}

	public EMicroservice getType() {
		return type;
	}

	@Override
	public String toString() {
		return "InstanceOnlineMessage{" +
				"messageUUID=" + getMessageUUID() +
				", senderUUID=" + getSenderUUID() +
				", type=" + type +
				'}';
	}
}
