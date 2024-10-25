package network;

import network.mM.ThreadController;
import messages.AbstractMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NetworkServiceFactory {
	private NetworkServiceFactory() {
		super();
	}

	private static Logger logger = LoggerFactory.getLogger(NetworkServiceFactory.class);

	private static final Queue<AbstractMessage> publishQueue = new ConcurrentLinkedQueue<>();
    private static final Map<Class<? extends AbstractMessage>, Callback<? extends AbstractMessage>> messageCallbacks = new ConcurrentHashMap<>();
    private static ThreadController threadController = null;

    /**
     * The NetworkServiceFactory must be initialized before any Subscribers/Publishers can be used.
     * This method will determine the port for incoming and neighbours automatically.
     * DONT USE IN CONTAINER MS
     * @param uuid The UUID of the microservice.
     */
    public static void initialize(UUID uuid) {
    	if (isInitialized()) {
			throw new RuntimeException("Trying to initialize main.network twice.");
		}
    	logger.info("Initialized NetworkService with UUID: " + uuid);
    	threadController = new ThreadController(publishQueue, messageCallbacks, uuid, Optional.empty());
    }

	/**
	 * The NetworkServiceFactory must be initialized before any Subscribers/Publishers can be used.
	 * USE IN CONTAINER MS
	 * @param uuid The UUID of the microservice.
	 * @param port The port to be opened for incoming connections.
	 */
	public static void initialize(UUID uuid, int port) {
		if (isInitialized()) {
			throw new RuntimeException("Trying to initialize main.network twice.");
		}
		logger.debug("Initialized with UUID: " + uuid + " and specified port: " + port);
		threadController = new ThreadController(publishQueue, messageCallbacks, uuid, Optional.of(port));
	}


	public static Publisher getPublisher() {
		if (!isInitialized()) {
			throw new IllegalStateException("trying to get Publisher from uninitialized NetworkService");
		}
		return new Publisher(publishQueue);
	}

	public static Subscriber getSubscriber() {
		if (!isInitialized()) {
			throw new IllegalStateException("trying to get Subscriber from uninitialized NetworkService");
		}
		return new Subscriber(messageCallbacks);
	}

	/**
	 * Terminates all network related threads and shuts the messaging framework down
	 */
	public static void terminateAllThreads() {
		if (!isInitialized()) {
    		logger.warn("Tried to terminate Threads when NetworkService was not initialized. Ignoring.");
    		return;
    	}
    	threadController.terminateAll();
    	threadController = null;
    	logger.info("Terminated network service");
    }

	/**
	 * Equivalent to terminateAllThreads
	 */
	public static void terminate() {
		terminateAllThreads();
	}

	/**
	 *
	 * @return True if the the NetworkService has been initialized. False otherwise
	 */
	public static boolean isInitialized() {
    	return (threadController != null);
    }
}
