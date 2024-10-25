package network.mM;

import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

import static org.junit.Assert.assertTrue;

public class NeighbourManagerTest {

	NeighbourManager neighbourManager;

	public NeighbourManagerTest() {
		super();
		List<NetworkNode> newList = new LinkedList<>();
		NetworkNode nn1;
		try {
			nn1 = new NetworkNode(InetAddress.getByName("localhost"), 10, "Hub");
			newList.add(nn1);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		NetworkConfig networkconfig = new NetworkConfig(newList, 15, true);
		neighbourManager = new NeighbourManager(networkconfig, UUID.randomUUID());
	}

	@Test
	public void addedDynamicNeighbours_check() throws UnknownHostException {
		UUID id = UUID.randomUUID();

		for (int i = 0; i < 1000; i++) {
			neighbourManager.addDynamicNeighbour(UUID.randomUUID(), InetAddress.getByName("localhost"), i, "Hub");
		}

		assertTrue(neighbourManager.getSize() == 1000);

	}

	@Test
	public void addedDynamicNeighbours_checkPort() throws UnknownHostException {
		UUID id = UUID.randomUUID();

		neighbourManager.addDynamicNeighbour(id, InetAddress.getByName("localhost"), 9000, "Hub");
		neighbourManager.addDynamicNeighbour(id, InetAddress.getByName("localhost"), 8999, "Vehicle");

		for (int i = 0; i < 1000; i++) {
			neighbourManager.addDynamicNeighbour(UUID.randomUUID(), InetAddress.getByName("localhost"), i, "Hub");
		}

		assertTrue(neighbourManager.getDynamicNeighbour(id).getPort() == 9000);

	}

	@Test
	public void addedDynamicNeighbours_removeAll_checkSize() throws UnknownHostException {
		UUID id = UUID.randomUUID();

		neighbourManager.addDynamicNeighbour(id, InetAddress.getByName("localhost"), 9000, "Hub");
		neighbourManager.addDynamicNeighbour(id, InetAddress.getByName("localhost"), 8999, "Vehicle");

		for (int i = 0; i < 1000; i++) {
			neighbourManager.addDynamicNeighbour(UUID.randomUUID(), InetAddress.getByName("localhost"), i, "Hub");
		}

		neighbourManager.removeAllDynamicNeighbours();

		assertTrue(neighbourManager.getSize() == 0);

	}

	@Test
	public void addedDynamicNeighbours_removeTwo_checkSize() throws UnknownHostException {

		UUID id = UUID.randomUUID();
		UUID id2 = UUID.randomUUID();

		neighbourManager.addDynamicNeighbour(id, InetAddress.getByName("localhost"), 8999, "Vehicle");
		neighbourManager.addDynamicNeighbour(id2, InetAddress.getByName("localhost"), 9000, "Hub");

		for (int i = 0; i < 1000; i++) {
			neighbourManager.addDynamicNeighbour(UUID.randomUUID(), InetAddress.getByName("localhost"), i, "Hub");
		}

		neighbourManager.removeDynamicNeighbour(id);
		neighbourManager.removeDynamicNeighbour(id2);

		assertTrue(neighbourManager.getSize() == 1000);

	}

	@Test
	public void addedDynamicNeighboursMap_checkSize() throws UnknownHostException {

		Map<UUID, NetworkNode> dynamicNeighbours = new HashMap<>();

		UUID id = UUID.randomUUID();
		UUID id2 = UUID.randomUUID();

		NetworkNode netwokNode1 = new NetworkNode(InetAddress.getByName("localhost"), 9000, "Vehicle");
		NetworkNode netwokNode2 = new NetworkNode(InetAddress.getByName("localhost"), 9001, "Hub");
		NetworkNode netwokNode3 = new NetworkNode(InetAddress.getByName("localhost"), 9002, "Container");

		dynamicNeighbours.put(id, netwokNode1);
		dynamicNeighbours.put(id2, netwokNode2);
		dynamicNeighbours.put(UUID.randomUUID(), netwokNode3);

		neighbourManager.setDynamicNeighbours(dynamicNeighbours);

		assertTrue(neighbourManager.getSize() == 3);

	}

	@Test
	public void addedDynamicNeighboursMap_removeAll_checkSize() throws UnknownHostException {

		Map<UUID, NetworkNode> dynamicNeighbours = new HashMap<>();

		UUID id = UUID.randomUUID();
		UUID id2 = UUID.randomUUID();

		NetworkNode netwokNode1 = new NetworkNode(InetAddress.getByName("localhost"), 9000, "Vehicle");
		NetworkNode netwokNode2 = new NetworkNode(InetAddress.getByName("localhost"), 9001, "Hub");
		NetworkNode netwokNode3 = new NetworkNode(InetAddress.getByName("localhost"), 9002, "Container");

		dynamicNeighbours.put(id, netwokNode1);
		dynamicNeighbours.put(id2, netwokNode2);
		dynamicNeighbours.put(UUID.randomUUID(), netwokNode3);

		neighbourManager.setDynamicNeighbours(dynamicNeighbours);

		neighbourManager.removeAllDynamicNeighbours();

		assertTrue(neighbourManager.getSize() == 0);

	}
}
