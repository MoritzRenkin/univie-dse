package ms_container;

import static org.junit.Assert.assertTrue;

import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import container.Container;
import container.Location;
import container.LocationHistory;
import main.Controller;
import messageUtil.ContainerInformation;
import messageUtil.EMicroservice;
import messages.AbstractMessage;
import messages.ContainerAtFinalDestination;
import messages.ContainerPositionUpdate;
import messages.InstanceOnlineMessage;
import messages.VehiclePositionUpdate;
import network.Callback;
import network.CallbackContainerPositionUpdate;
import network.Subscriber;

public class ContainerTest {

	private static Logger logger = LoggerFactory.getLogger(ContainerTest.class);
	private Controller controller;
	private static final UUID containerId = UUID.fromString("d23da7da-5355-11eb-ae93-0242ac130002");
	private FileWriter fw; 

	@Test
	public void containerPositionUpdate() throws InterruptedException {

		controller = new Controller(containerId, 10, 9500, UUID.fromString("acd29975-5976-49da-bfba-33f919a90a9b"),
				UUID.fromString("bb5a14c2-5346-11eb-ae93-0242ac130002"));

		Thread t = new Thread(() -> {
			controller.startMicroService();
		});
		t.start();
		Thread.sleep(2000);

		assertTrue(
				controller.getNetworkController().getSubscriber().isSubscriptionPresent(ContainerPositionUpdate.class));
		assertTrue(
				controller.getNetworkController().getSubscriber().isSubscriptionPresent(ContainerAtFinalDestination.class));
		t.interrupt();

	}

	@Test
	public void callback_containerPositionUpdate() throws InterruptedException {

		Location curr = new Location(UUID.randomUUID());
		Location dest = new Location(UUID.randomUUID());

		UUID self = UUID.randomUUID();

		List<Location> li = new ArrayList<>();
		li.add(curr);

		LocationHistory lh = new LocationHistory(li);
		Container cont = new Container(curr, dest, 10, self, lh);
		logger.info("Old loc: " + cont.getCurrentLocation().getID());
		FileWriter fw;
		try {
			fw = new FileWriter("ContainerUUID_" + cont.getID().toString() + ".txt", false);
			
			Callback<ContainerPositionUpdate> callback = new CallbackContainerPositionUpdate(cont,fw);

			UUID id1 = UUID.randomUUID();
			UUID id2 = UUID.randomUUID();
			UUID id3 = UUID.randomUUID();
			
			ContainerInformation ci = new ContainerInformation(self, 10, id1, id2, id3);

			ContainerPositionUpdate mes = new ContainerPositionUpdate(UUID.randomUUID(), ci, false);

			callback.onResponse(mes);
			logger.info("First callback: Container position now: " + cont.getCurrentLocation().getID() + " should be: "
					+ ci.getCurrentHub());
			logger.debug("History should be " + cont.getCurrentLocation().getID() + "in fact: "
					+ cont.getLocationHistory().getLocationHistory()
							.get(cont.getLocationHistory().getLocationHistory().size() - 1).getID().toString());

			// First callback test
			assertTrue(cont.getCurrentLocation().getID().equals(ci.getCurrentHub()));
			assertTrue(cont.getCurrentLocation().getID().equals(cont.getLocationHistory().getLocationHistory()
					.get(cont.getLocationHistory().getLocationHistory().size() - 1).getID()));
			UUID id5 = UUID.randomUUID();
			UUID id4 = UUID.randomUUID();
			UUID id6 = UUID.randomUUID();
			ContainerInformation ci2 = new ContainerInformation(self, 10, id5, id4, id6);

			ContainerPositionUpdate mes2 = new ContainerPositionUpdate(UUID.randomUUID(), ci2, false);

			
			Thread.sleep(10000);
			callback.onResponse(mes2);

			logger.info("Second callback: Container position now: " + cont.getCurrentLocation().getID() + " should be: "
					+ ci2.getCurrentHub());
			// Second callback test
			assertTrue(cont.getCurrentLocation().getID().equals(ci2.getCurrentHub()));
			assertTrue(cont.getCurrentLocation().getID().equals(cont.getLocationHistory().getLocationHistory()
					.get(cont.getLocationHistory().getLocationHistory().size() - 1).getID()));
			int before = cont.getLocationHistory().size();
			callback.onResponse(mes2);
			int after = cont.getLocationHistory().size();

			// If Location is the same as it was before, LocalHistory should not be updated
			assertTrue(before == after);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	@Test
	public void testContainer() throws InterruptedException {

		controller = new Controller(containerId, 10, 9500, UUID.fromString("acd29975-5976-49da-bfba-33f919a90a9b"),
				UUID.fromString("bb5a14c2-5346-11eb-ae93-0242ac130002"));

		Thread t = new Thread(() -> {
			controller.startMicroService();
		});
		
		t.start();
		Thread.sleep(2000);
		
		UUID id1 = UUID.randomUUID();
		UUID id2 = UUID.randomUUID();
		UUID id3 = UUID.randomUUID();
		
		ContainerInformation ci = new ContainerInformation(containerId, 10, id1, id2, id3);
		AbstractMessage newMessage = new ContainerPositionUpdate(UUID.fromString("acd29975-5976-49da-bfba-33f919a90a9b"), ci);

		Socket socket;
		try {
			socket = new Socket("localhost",9500);
			OutputStream outputStream = socket.getOutputStream();
	        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

	        objectOutputStream.writeObject(newMessage);

	        objectOutputStream.close();
	        outputStream.close();
	        socket.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		 id1 = UUID.randomUUID();
		 id2 = UUID.randomUUID();
		 id3 = UUID.randomUUID();

		ContainerInformation ci2 = new ContainerInformation(containerId, 10, id1, id2, id3);
		AbstractMessage newMessage2 = new ContainerPositionUpdate(UUID.fromString("acd29975-5976-49da-bfba-33f919a90a9b"), ci2);
		
		Thread.sleep(1000);
		try {
			socket = new Socket("localhost",9500);
			OutputStream outputStream = socket.getOutputStream();
	        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

	        objectOutputStream.writeObject(newMessage2);

	        objectOutputStream.close();
	        outputStream.close();
	        socket.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Thread.sleep(1000);
		t.interrupt();

        
	}

}
