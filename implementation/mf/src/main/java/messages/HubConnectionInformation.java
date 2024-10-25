package messages;

import java.util.Collection;
import java.util.UUID;

import messageUtil.NodeConnection;

/**
 * At the beginning of the setup the Hubs need to generate the whole map to route containers. 
 * Therefore, Hubs publish the Hubs/Stations  they  are  connected  to  and  receive  this information 
 * from the other hubs.
 *
 */
public class HubConnectionInformation extends AbstractMessage {

	private static final long serialVersionUID = -1110772224021133723L;
	private final UUID hub;
	private final Collection<NodeConnection> connections;

	public HubConnectionInformation(UUID senderUUID, Collection<NodeConnection> connections, UUID hub) {
		super(senderUUID);
		this.connections = connections;
		this.hub = hub;

	}
	public UUID getHub() {
		return hub;
	}

	public Collection<NodeConnection> getConnections() {
		return connections;
	}
	public HubConnectionInformation(UUID senderUUID, UUID hub, Collection<NodeConnection> connections) {
		super(senderUUID);
		this.hub = hub;
		this.connections = connections;
	}

	@Override
	public String toString() {
		return "HubConnectionInformation{" +
				"messageUUID=" + getMessageUUID() +
				", senderUUID=" + getSenderUUID() +
				", hub=" + hub +
				", connections=" + connections +
				'}';
	}
}
