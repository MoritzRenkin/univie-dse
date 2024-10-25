package network.mM;

import exceptions.NoSuchNeighbourException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class NeighbourManager {

	private static Logger logger = LoggerFactory.getLogger(NeighbourManager.class);
	private Map<UUID, NetworkNode> dynamicNeighbours = new ConcurrentHashMap<>();
	private final int port;
	private final boolean bridgingActivated;
	private final List<NetworkNode> staticNeighbours;
	private final UUID selfUUID;

	public NeighbourManager(NetworkConfig networkConfig, UUID selfUUID) {
		super();
		this.port = networkConfig.getPort();
		this.bridgingActivated = networkConfig.isBridgingActivated();
		this.staticNeighbours = networkConfig.getStaticNeighbours();
		this.selfUUID = selfUUID;
	}

	public void addDynamicNeighbour(UUID id, InetAddress ip, int port, String nodeType) {
		if (dynamicNeighbours.get(id) == null) {
			NetworkNode newNode = new NetworkNode(ip, port, nodeType);
			this.dynamicNeighbours.put(id, newNode);

			logger.debug("New dynamicNeighbour added: " + id);
		} else {
			logger.warn("Could not add neighbour " + id + " because it was already present.");
		}
	}

	public NetworkNode removeDynamicNeighbour(UUID uuid) {
		logger.debug("dynamicNeighbour removed: " + uuid);
		return dynamicNeighbours.remove(uuid);
	}

	public void removeAllDynamicNeighbours() {
		dynamicNeighbours.clear();
		logger.debug("All dynamicNeighbours cleared");
	}

	public NetworkNode getDynamicNeighbour(UUID uuid) {
		NetworkNode neighbour = dynamicNeighbours.get(uuid);
		if (neighbour == null) {
			throw new NoSuchNeighbourException("Dynamic Neighbour with UUID " + uuid
					+ " is not present in NeighbourManager, present dynamic neighbour uuids:\n" + dynamicNeighbours);
		}
		return neighbour;
	}

	public int getSize() {

		return dynamicNeighbours.size();
	}

	public Map<UUID, NetworkNode> getDynamicNeighbours() {
		return dynamicNeighbours;
	}



	public void setDynamicNeighbours(Map<UUID, NetworkNode> dynamicNeighbours) {
		this.dynamicNeighbours = dynamicNeighbours;
	}

	public int getPort() {
		return port;
	}

	public UUID getSelfUUID() {
		return selfUUID;
	}

	public boolean isBridgingActivated() {
		return bridgingActivated;
	}

	public List<NetworkNode> getStaticNeighbours() {
		return staticNeighbours;
	}

}
