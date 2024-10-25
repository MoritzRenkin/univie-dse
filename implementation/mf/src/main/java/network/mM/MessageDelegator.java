package network.mM;

import exceptions.NoSuchNeighbourException;
import network.Callback;
import messages.AbstractMessage;
import messages.ContainerHandover;
import messages.DynamicNeighbourHandover;
import messages.NewContainerAtSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class MessageDelegator implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(MessageDelegator.class);

	private ExecutorService callbackExecutor = Executors.newCachedThreadPool();
	private final MessageUniquenessChecker uniqueChecker = new MessageUniquenessChecker();

	private final Queue<AbstractMessage> publishQueue;
	private final Map<Class<? extends AbstractMessage>, Callback<? extends AbstractMessage>> messageCallbacks;
	private final Queue<AbstractMessage> incomingMessagesQueue;

	private final NeighbourManager neighbourManager;

	private boolean terminationSignal = false;

	public MessageDelegator(Queue<AbstractMessage> publishQueue,
							Map<Class<? extends AbstractMessage>,
									Callback<? extends AbstractMessage>> messageCallbacks,
							Queue<AbstractMessage> incomingMessagesQueue,
							NeighbourManager neighbourManager) {

		super();
		this.publishQueue = publishQueue;
		this.messageCallbacks = messageCallbacks;
		this.incomingMessagesQueue = incomingMessagesQueue;
		this.neighbourManager = neighbourManager;
	}

	@Override
	public void run() {
		if (terminationSignal) {
			throw new IllegalStateException("MessageDelegator was terminated before start-up. Did you start a delegator after calling terminate()?");
		}

		while(!terminationSignal) {

			while(!incomingMessagesQueue.isEmpty() && !terminationSignal) { //this is only safe bc this class is the only consumer of incomingMessageQueue
				AbstractMessage newMessage = incomingMessagesQueue.poll();
				logger.debug("Received message: " + newMessage);

				if (uniqueChecker.isMessageUnique(newMessage)) {
					handleInternally(newMessage);

					if (!newMessage.getSenderUUID().equals(neighbourManager.getSelfUUID())) {
						invokeMessageCallback(newMessage);
						forwardMessage(newMessage);
					}
				}
			}

			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				logger.warn("Thread interrupted during sleep period");
				break;
			}
		}
		callbackExecutor.shutdownNow();
	}

	private void invokeMessageCallback(AbstractMessage newMessage) {

		Class<? extends AbstractMessage> messageClass = newMessage.getClass();
		Callback callback = messageCallbacks.get(messageClass);

		if (callback != null) { // checking for null is safer than checking existence before getting from map;
			logger.debug("Invoking Callback for Message with UUID " + newMessage.getMessageUUID());
			CallbackJob job = new CallbackJob(callback, newMessage);
			callbackExecutor.execute(job);
		}
	}

	private void forwardMessage(AbstractMessage message) {
		if (neighbourManager.isBridgingActivated()) {
			logger.debug("Bridging message with UUID " + message.getMessageUUID());
			publishQueue.add(message);
		}

	}

	/**
	 * Executes network internal logic (concerning the creation/handover of neighbours for bridging)
	 *  If a NewContainerAtSource is handled, a neighbour is added.
	 *  If a ContainerHandover is handled, the ip and port of that container are looked up and a DynamicNeighbourHandover is sent to the target.
	 */
	private void handleInternally(AbstractMessage message) {
		if (message instanceof NewContainerAtSource) {
			if (message.getSenderUUID().equals(neighbourManager.getSelfUUID())) {
				NewContainerAtSource castMessage = (NewContainerAtSource) message;

				UUID neighbourUUID = castMessage.getContainerInformation().getContainerId();
				InetAddress ip = castMessage.getIp();
				int port = castMessage.getPort();

				logger.info("Detected new neighbour (container) to add. UUID : " + neighbourUUID);
				neighbourManager.addDynamicNeighbour(neighbourUUID, ip, port, "Container");
			}

		} else if (message instanceof ContainerHandover) {
			if(message.getSenderUUID().equals(neighbourManager.getSelfUUID())) {
				ContainerHandover containerHandover = (ContainerHandover) message;
				publishDynamicNeighbourHandover(containerHandover);
			}

		} else if (message instanceof DynamicNeighbourHandover) {
			DynamicNeighbourHandover dynamicHandover = (DynamicNeighbourHandover) message;
			if (dynamicHandover.getTakerUUID().equals(neighbourManager.getSelfUUID())) {
				UUID neighbourUUID = dynamicHandover.getNeighbourUUID();
				InetAddress ip = dynamicHandover.getNeighbourIp();
				int port = dynamicHandover.getNeighbourPort();

				logger.info("Adding Dynamic neighbour with UUID " + neighbourUUID);
				neighbourManager.addDynamicNeighbour(neighbourUUID, ip, port, "Unknown");
			}
		}
	}

	private void publishDynamicNeighbourHandover(ContainerHandover containerHandover) {
		try {
			//Sending DynamicNeighbourHandover in order to convey IP and Port to the taker of the container;
			NetworkNode neighbour = neighbourManager.getDynamicNeighbour(containerHandover.getContainerUUID());
			DynamicNeighbourHandover handoverMessage = new DynamicNeighbourHandover(
					containerHandover.getSenderUUID(),
					containerHandover.getTakerUUID(),
					containerHandover.getContainerUUID(),
					neighbour.getIp(),
					neighbour.getPort());
			publishQueue.add(handoverMessage);

			logger.info("Handed over dynamic neighbour " + handoverMessage.toString());
			neighbourManager.removeDynamicNeighbour(containerHandover.getContainerUUID());
		} catch (NoSuchNeighbourException e) {
			logger.error("Unable to handover container with UUID" + containerHandover.getContainerUUID() + " as it is not one of your neighbours!\n"
					+ "Current dynamic neighbours neighbours: " + neighbourManager.getDynamicNeighbours() + '\n'
					+ "Continuing without handover.", e);
		}
	}

	public void terminate() { //Thread safe operation
		terminationSignal = true;
	}
}
