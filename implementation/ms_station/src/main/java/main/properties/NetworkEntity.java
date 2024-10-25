package main.properties;


import java.util.UUID;

public class NetworkEntity {

	private final UUID id;
	private final int port;
	private final boolean bridgingActivated;
	private final String nodeType;
	private final String ip;

	public NetworkEntity(UUID id, String ip, int port, boolean bridgingActivated, String nodeType) {
		this.id = id;
		this.bridgingActivated = bridgingActivated;
		this.port = port;
		this.ip = ip;
		this.nodeType = nodeType;
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

    public String getIP() {
		return this.ip;
    }
}
