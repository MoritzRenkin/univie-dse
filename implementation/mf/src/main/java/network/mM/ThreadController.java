package network.mM;

import network.Callback;
import messages.AbstractMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ThreadController {
	private static Logger logger = LoggerFactory.getLogger(ThreadController.class);

	private Thread messageDelegatorThread;
	private Thread networkInThread;
	private Thread networkOutThread;

	private MessageDelegator delegator;
	private NetworkOutThread networkOut;

	public ThreadController(Queue<AbstractMessage> publishQueue,
							Map<Class<? extends AbstractMessage>,
							Callback<? extends AbstractMessage>> messageCallbacks,
							UUID uuid,
							Optional<Integer> optinalPort
							) {

		Queue<AbstractMessage> incomingMessagesQueue = new ConcurrentLinkedQueue<>();

		// if no port is specified, read config. else just use the port without reading config
		NetworkConfig networkConfig = optinalPort.map(
				integer -> new NetworkConfig(new ArrayList<>(), integer)
		).orElseGet(
				() -> new JsonConvert(uuid).getConf());

		NeighbourManager neighbourManager = new NeighbourManager(networkConfig, uuid);

		delegator = new MessageDelegator(publishQueue, messageCallbacks, incomingMessagesQueue, neighbourManager);
		messageDelegatorThread = new Thread(delegator);
		messageDelegatorThread.start();

		NetworkInThread networkIn = new NetworkInThread(incomingMessagesQueue, neighbourManager.getPort());
		networkInThread = new Thread(networkIn);
		networkInThread.setDaemon(true);
		networkInThread.start();

		networkOut = new NetworkOutThread(publishQueue, neighbourManager);
		networkOutThread = new Thread(networkOut);
		networkOutThread.start();
	}

	public void terminateAll() {
		logger.info("Termination signal sent to Threads. Note: Daemon Threads might only stop when the main Thread exits.");
		delegator.terminate();
		networkOut.terminate();

		messageDelegatorThread.interrupt();
		networkInThread.interrupt();
		networkOutThread.interrupt();
	}
}
