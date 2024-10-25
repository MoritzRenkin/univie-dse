package network.mM;

import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import messageUtil.EMicroservice;
import messages.AbstractMessage;
import messages.InstanceOnlineMessage;

public class NetworkInAndOutTest implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger logger = LoggerFactory.getLogger(NetworkInAndOutTest.class);
	
	NetworkInThread networkinthread;
	NetworkOutThread networkoutthread;
	NeighbourManager neighbourManager;
	private Thread networkInThread;
	private Thread networkOutThread;
	
	@Test
	public void send_and_receive() {
		
		List<NetworkNode> newList = new LinkedList<>();
		NetworkNode nn1;
		try {
			nn1 = new NetworkNode(InetAddress.getByName("localhost"), 45000, "Hub");
			newList.add(nn1);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		NetworkConfig networkconfig = new NetworkConfig(newList, 45000, true);
		neighbourManager = new NeighbourManager(networkconfig, UUID.randomUUID());
		Queue<AbstractMessage> incomingMessagesQueue = new ConcurrentLinkedQueue<>();
		logger.debug("Starting NetworkInThread");
		
		networkinthread = new NetworkInThread(incomingMessagesQueue, neighbourManager.getPort());
		networkInThread = new Thread(networkinthread);
		networkInThread.setDaemon(true);
		networkInThread.start();
		
		
		
		logger.debug("Waiting 2 seconds");
		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		logger.debug("Starting NetworkOutThread");
		Queue<AbstractMessage> publishQueue =  new ConcurrentLinkedQueue<>();
		AbstractMessage newMessage = new InstanceOnlineMessage(UUID.fromString("55d62c66-c067-4602-a48d-593c0f4dcb1a"), EMicroservice.STATION);
		AbstractMessage newMessage1 = new InstanceOnlineMessage(UUID.fromString("dbd8a3c0-801e-4765-a734-a6a1757b64ba"), EMicroservice.HUB);

		publishQueue.add(newMessage);
		
		System.out.println(publishQueue);
		
		networkoutthread = new NetworkOutThread(publishQueue, neighbourManager);
		networkOutThread = new Thread(networkoutthread);
		networkOutThread.start();
		
		logger.debug("End");
		
		
		
		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		publishQueue.add(newMessage1);
		
		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		assertTrue(incomingMessagesQueue.size()==2);
		//assertTrue(incomingMessagesQueue.peek()));

	}
}
