package messages;


import java.net.InetAddress;
import java.util.UUID;

import messageUtil.ContainerInformation;

/**
 * If a Station (Source) gets a new container in, it has to inform the hubs about the new container so they can pick it up. 
 * This message is also processed internally by the MF to ensure that messages are bridged to the new microservice. 
 *
 */
public class NewContainerAtSource extends AbstractMessage {

	private static final long serialVersionUID = 6551434853075275277L;

	private final ContainerInformation containerInformation;
	private final InetAddress ip;
	private final int port;
	
	
	public NewContainerAtSource(UUID senderUUID, ContainerInformation containerInformation, InetAddress ip, int port) {
		super(senderUUID);
		this.containerInformation = containerInformation;
		this.ip = ip;
		this.port = port;
	}


	public ContainerInformation getContainerInformation() {
		return containerInformation;
	}


	public InetAddress getIp() {
		return ip;
	}


	public int getPort() {
		return port;
	}


	@Override
	public String toString() {
		return "NewContainerAtSource{" +
				"messageUUID=" + getMessageUUID() +
				", senderUUID=" + getSenderUUID() +
				", containerInformation=" + containerInformation +
				", ip=" + ip +
				", port=" + port +
				'}';
	}
}
