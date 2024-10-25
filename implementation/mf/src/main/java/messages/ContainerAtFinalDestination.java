package messages;

import java.util.UUID;

import messageUtil.ContainerInformation;

/**
 * The station (destination) sends out that it received a container at the final destination.
 *
 */
public class ContainerAtFinalDestination extends AbstractMessage {
	
	private static final long serialVersionUID = -5724055907471174134L;
	
	private ContainerInformation containerInformation;

	public ContainerAtFinalDestination(UUID senderUUID, ContainerInformation containerInformation) {
		super(senderUUID);
		this.containerInformation = containerInformation;
	}

	public ContainerInformation getContainerInformation() {
		return containerInformation;
	}

	@Override
	public String toString() {
		return "ContainerAtFinalDestination{" +
				"messageUUID=" + getMessageUUID() +
				", senderUUID=" + getSenderUUID() +
				", containerInformation=" + containerInformation +
				'}';
	}
}
