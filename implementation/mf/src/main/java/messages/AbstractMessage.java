package messages;

import java.io.Serializable;
import java.util.UUID;

public abstract class AbstractMessage implements Serializable{
	
	private final UUID messageUUID = UUID.randomUUID();
	private final UUID senderUUID;
	private String sentBy; //for debugging only
	
	
	
	public AbstractMessage(UUID senderUUID) {
		super();
		this.senderUUID = senderUUID;
	}



	public String getSentBy() {
		return sentBy;
	}

	public void setSentBy(String sentBy) {
		this.sentBy = sentBy;
	}

	public UUID getMessageUUID() {
		return messageUUID;
	}

	public UUID getSenderUUID() {
		return senderUUID;
	}
	
	
}
