package main.properties;

import java.util.UUID;


public class NetworkEntity {

	private final UUID id;
	private final int port;
	private final boolean bridgingActivated;
	private final String nodeType;


	public NetworkEntity(UUID id, int port, boolean bridgingActivated, String nodeType) {
		super();
		this.id = id;
		this.bridgingActivated = bridgingActivated;
		this.port = port;
		this.nodeType = nodeType;
	}

	public NetworkEntity(UUID id, int port, String nodeType) {
		this(id, port, true, nodeType);
	}

	public boolean isBridgingActivated() {
		return bridgingActivated;
	}


	public UUID getId() {
		return id;
	}

	public int getPort() {
		return port;
	}

	public String getNodeType() {
		return nodeType;
	}
}
