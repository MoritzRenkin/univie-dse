package messages;

import java.util.UUID;

import messageUtil.ContainerInformation;

/**
 * Any Service currently transporting or storing a container send out this message to report the current location of the
 * container to the Hub operator.
 */
public class ContainerPositionUpdate extends AbstractMessage {
	
	private static final long serialVersionUID = 4035439031368901314L;
	
	private ContainerInformation containerInformation;
	private boolean inTravel = false;


	public ContainerPositionUpdate(UUID senderUUID, ContainerInformation containerInformation) {
		super(senderUUID);
		this.containerInformation = containerInformation;
	}

	public ContainerPositionUpdate(UUID senderUUID, ContainerInformation containerInformation, boolean inTravel) {
		super(senderUUID);
		this.containerInformation = containerInformation;
		this.inTravel = inTravel;
	}



	public ContainerInformation getContainerInformation() {
		return containerInformation;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public boolean isInTravel() {
		return inTravel;
	}

	@Override
	public String toString() {
		return "ContainerPositionUpdate{" +
				"messageUUID=" + getMessageUUID() +
				", senderUUID=" + getSenderUUID() +
				", containerInformation=" + containerInformation +
				", inTravel=" + inTravel +
				'}';
	}
}
