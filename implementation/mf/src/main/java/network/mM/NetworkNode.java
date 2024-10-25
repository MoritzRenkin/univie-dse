package network.mM;

import java.net.InetAddress;

class NetworkNode {
	private final InetAddress ip;
	private final int port;
	private final String nodeType; // can be changed to enum in future

	public NetworkNode(InetAddress ip, int port, String nodeType) {
		this.ip = ip;
		this.port = port;
		this.nodeType = nodeType;
	}

	public InetAddress getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}

	public String getNodeType() {
		return nodeType;
	}

	@Override
	public String toString() {
		return "NetworkNode{" +
				"ip=" + ip +
				", port=" + port +
				", nodeType='" + nodeType + '\'' +
				'}';
	}
}
