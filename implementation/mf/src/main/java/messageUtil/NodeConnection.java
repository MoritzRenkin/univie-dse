package messageUtil;

import java.io.Serializable;
import java.util.UUID;

public class NodeConnection implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7637184466630628061L;
	private boolean hub;
	private int distance;
	private UUID nodeId;
	
	public NodeConnection(boolean hub, int distance, UUID nodeId) {
		this.hub = hub;
		this.distance = distance;
		this.nodeId = nodeId;
	}

	public boolean isHub() {
		return hub;
	}

	public int getDistance() {
		return distance;
	}

	public UUID getNodeId() {
		return nodeId;
	}
	
}
