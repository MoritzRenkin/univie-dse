package network.mM;

import network.Callback;
import messageUtil.ContainerInformation;
import messages.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;


public class MessageDelegatorTest {
	private static Queue<AbstractMessage> publishQueue = new ConcurrentLinkedQueue<>();
	private static Map<Class<? extends AbstractMessage>, Callback<? extends AbstractMessage>> messageCallbacks = new ConcurrentHashMap<>();
	private static Queue<AbstractMessage> incomingMessagesQueue = new ConcurrentLinkedQueue<>();
	private static UUID selfUUID = UUID.randomUUID();
	private static NeighbourManager neighbourManager = new NeighbourManager(new NetworkConfig(new ArrayList<>(), 1), selfUUID);

	private static ContainerInformation randomContainerInformation = new ContainerInformation(UUID.randomUUID(), 1, UUID.randomUUID(), UUID.randomUUID(), null);


	private static Thread thread;
	private static MessageDelegator delegator = new MessageDelegator(publishQueue, messageCallbacks, incomingMessagesQueue, neighbourManager);

	private final int defaultSleepTime = 250; //milliseconds

	@BeforeClass
	public static void startThread() {
		thread = new Thread(delegator);
		thread.start();
	}

	@AfterClass
	public static void terminateThread() {
		delegator.terminate();
	}

	@After
	public void clearCollections() {
		publishQueue.clear();
		messageCallbacks.clear();
		incomingMessagesQueue.clear();
		neighbourManager.removeAllDynamicNeighbours();
	}

	@Test
	public void threadRunningWithoutLoad_callTerminate_threadTerminates() throws InterruptedException {

		MessageDelegator localDelegator = new MessageDelegator(publishQueue, messageCallbacks, incomingMessagesQueue, neighbourManager);
		Thread localThread = new Thread(localDelegator);
		localThread.start();

		Thread.sleep(500);
		localDelegator.terminate();

		Thread.sleep(defaultSleepTime);
		assertFalse(localThread.isAlive());
	}


	@Test
	public void manyUniqueMessagesReceived_bridgesAll() throws InterruptedException {
		final int messageCount = 200;

		Set<AbstractMessage> excectedMessages = new HashSet<>();
		for(int i=0; i<messageCount; ++i) {
			AbstractMessage newMessage = new AbstractMessage(UUID.randomUUID()) { };

			excectedMessages.add(newMessage);
			incomingMessagesQueue.add(newMessage);
		}

		Thread.sleep(defaultSleepTime * 10);

		assertEquals(0, incomingMessagesQueue.size());
		assertEquals(messageCount, publishQueue.size());

		assertTrue(excectedMessages.containsAll(publishQueue));
	}


	@Test
	public void sameMessagesReceived_bridgesOnlyOnce() throws InterruptedException {
		AbstractMessage dummyMessage = new AbstractMessage(UUID.randomUUID()) { };
		AbstractMessage otherDummyMessage = new AbstractMessage(UUID.randomUUID()) { };

		incomingMessagesQueue.add(dummyMessage);
		incomingMessagesQueue.add(dummyMessage);
		incomingMessagesQueue.add(otherDummyMessage);
		incomingMessagesQueue.add(dummyMessage);
		incomingMessagesQueue.add(dummyMessage);

		Thread.sleep(defaultSleepTime);

		assertEquals(0, incomingMessagesQueue.size());
		assertEquals(publishQueue.size(), 2);

		AbstractMessage firstMessageSent = publishQueue.poll();
		assertEquals(dummyMessage.getMessageUUID(), firstMessageSent.getMessageUUID());
	}


	@Test
	public void subscribtionPresent_receivedSubscribedMessageTypeOnce_invokedCallbackOnce() throws InterruptedException {
		AtomicInteger callbackInvokationCount = new AtomicInteger(0);
		Callback<VehicleOrder> callback = new CounterCallback(callbackInvokationCount);

		messageCallbacks.put(VehicleOrder.class, callback);
		incomingMessagesQueue.add(new VehicleOrder(UUID.randomUUID(), UUID.randomUUID(),UUID.randomUUID(), null, 0));

		Thread.sleep(defaultSleepTime);

		assertEquals(1, messageCallbacks.size());
		assertEquals(1, callbackInvokationCount.get());
	}

	@Test
	public void subscriptionPresent_receivedSelfSentMessage_invokesNothing() throws InterruptedException {
		AtomicInteger callbackInvokationCount = new AtomicInteger(0);
		Callback<VehicleOrder> callback = new CounterCallback(callbackInvokationCount);

		messageCallbacks.put(VehicleOrder.class, callback);
		AbstractMessage dummyMessage = new VehicleOrder(selfUUID, UUID.randomUUID(), UUID.randomUUID(), null, 0);
		incomingMessagesQueue.add(dummyMessage);
		incomingMessagesQueue.add(dummyMessage);

		Thread.sleep(defaultSleepTime);

		assertEquals(1, messageCallbacks.size());
		assertEquals(0, callbackInvokationCount.get());

	}

	@Test
	public void subscribtionPresent_receivedSameMessageTwice_invokedCallbackOnlyOnce() throws InterruptedException {
		AtomicInteger callbackInvokationCount = new AtomicInteger(0);
		Callback<VehicleOrder> callback = new CounterCallback(callbackInvokationCount);

		messageCallbacks.put(VehicleOrder.class, callback);
		AbstractMessage dummyMessage = new VehicleOrder(UUID.randomUUID(), UUID.randomUUID(), null, null, 0);
		incomingMessagesQueue.add(dummyMessage);
		incomingMessagesQueue.add(dummyMessage);

		Thread.sleep(defaultSleepTime);

		assertEquals(1, messageCallbacks.size());
		assertEquals(1, callbackInvokationCount.get());
	}


	@Test
	public void subscriptionPresent_receivedUnsubscribedMessage_invokesNothing() throws InterruptedException {
		AtomicInteger callbackInvokationCount = new AtomicInteger(0);
		Callback<VehicleOrder> callback = new CounterCallback(callbackInvokationCount);

		messageCallbacks.put(VehicleOrder.class, callback);
		incomingMessagesQueue.add(new InstanceOnlineMessage(UUID.randomUUID(), null));

		Thread.sleep(defaultSleepTime);

		assertEquals(1, messageCallbacks.size());
		assertEquals(0, callbackInvokationCount.get());
	}

	@Test
	public void subscriptionPresent_receive2UniqueSubscribedMessages_invokesCallbackTwice() throws InterruptedException {
		AtomicInteger callbackInvokationCount = new AtomicInteger(0);
		Callback<VehicleOrder> callback = new CounterCallback(callbackInvokationCount);

		messageCallbacks.put(VehicleOrder.class, callback);
		incomingMessagesQueue.add(new VehicleOrder(UUID.randomUUID(), UUID.randomUUID(), null, null, 0));
		incomingMessagesQueue.add(new VehicleOrder(UUID.randomUUID(), UUID.randomUUID(), null, null, 0));

		Thread.sleep(defaultSleepTime);

		assertEquals(1, messageCallbacks.size());
		assertEquals(2, callbackInvokationCount.get());
	}


	@Test
	public void receivedNewContainerAtSourceFromItself_addsNeighbour() throws UnknownHostException, InterruptedException {
		InetAddress containerIp = InetAddress.getByName("1.2.3.4");
		int containerPort = 12;

		NewContainerAtSource message = new NewContainerAtSource(selfUUID, randomContainerInformation, containerIp,containerPort);
		incomingMessagesQueue.add(message);

		Thread.sleep(defaultSleepTime);

		assertEquals(1, neighbourManager.getDynamicNeighbours().size());

		NetworkNode neighbour = neighbourManager.getDynamicNeighbour(randomContainerInformation.getContainerId());
		assertEquals(containerIp, neighbour.getIp());
		assertEquals(containerPort, neighbour.getPort());
	}


	@Test
	public void neighbourPresent_receivedContainerHandoverFromItself_removesNeighbour() throws UnknownHostException, InterruptedException {
		InetAddress containerIp = InetAddress.getByName("1.2.3.4");
		int containerPort = 12;
		UUID containerUUID = UUID.randomUUID();

		neighbourManager.addDynamicNeighbour(containerUUID, containerIp, containerPort, "");
		assertEquals(1, neighbourManager.getDynamicNeighbours().size());

		ContainerHandover message = new ContainerHandover(selfUUID, UUID.randomUUID(), containerUUID);
		incomingMessagesQueue.add(message);

		Thread.sleep(defaultSleepTime);

		assertEquals(0, neighbourManager.getDynamicNeighbours().size());

	}

	@Test
	public void neighbourPresent_receivedContainerHandoverFromItself_sendsDynamicNeighbourHandover() throws UnknownHostException, InterruptedException  {
		InetAddress containerIp = InetAddress.getByName("1.2.3.4");
		int containerPort = 12;
		UUID containerUUID = UUID.randomUUID();

		neighbourManager.addDynamicNeighbour(containerUUID, containerIp, containerPort, "");
		assertEquals(1, neighbourManager.getDynamicNeighbours().size());

		ContainerHandover message = new ContainerHandover(selfUUID, UUID.randomUUID(), containerUUID);
		incomingMessagesQueue.add(message);

		Thread.sleep(defaultSleepTime);

		assertEquals(1, publishQueue.size());

		AbstractMessage messageSent = publishQueue.poll();
		assertTrue(messageSent instanceof DynamicNeighbourHandover);

		DynamicNeighbourHandover dynamicHandover = (DynamicNeighbourHandover) messageSent;
		assertEquals(containerPort, dynamicHandover.getNeighbourPort());
		assertEquals(containerIp, dynamicHandover.getNeighbourIp());
		assertEquals(containerUUID, dynamicHandover.getNeighbourUUID());

	}

	@Test
	public void receivedDynamicNeighbourHandoverAsTaker_addsNeighbour() throws UnknownHostException, InterruptedException {
		InetAddress containerIp = InetAddress.getByName("1.2.3.4");
		int containerPort = 12;
		UUID containerUUID = UUID.randomUUID();

		DynamicNeighbourHandover message = new DynamicNeighbourHandover(UUID.randomUUID(), selfUUID, containerUUID, containerIp, containerPort);
		incomingMessagesQueue.add(message);

		Thread.sleep(defaultSleepTime);

		assertEquals(1, neighbourManager.getDynamicNeighbours().size());

		NetworkNode neighbour = neighbourManager.getDynamicNeighbour(containerUUID);
		assertEquals(containerIp, neighbour.getIp());
		assertEquals(containerPort, neighbour.getPort());

	}
}
