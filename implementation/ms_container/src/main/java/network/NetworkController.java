package network;

import java.io.FileWriter;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import container.Container;
import container.Location;
import messages.ContainerPositionUpdate;
import messages.ContainerAtFinalDestination;

public class NetworkController {
	
	private static Logger logger = LoggerFactory.getLogger(NetworkController.class);
	private Queue<ContainerPositionUpdate> callbackQueue = new ConcurrentLinkedQueue<>();
	private Queue<ContainerAtFinalDestination> callbackContainerAtFinalDestination = new ConcurrentLinkedQueue<>();
	private UUID uuid;
	private Subscriber subscriber;
	private Publisher publisher;
	private Container container;
	private FileWriter fw;

	Callback<ContainerPositionUpdate> callbackPosition;
	Callback<ContainerAtFinalDestination> callbackAtFinal;

	public NetworkController(UUID uuid, int port, Container container, FileWriter fw) {
		this.container = container;
		NetworkServiceFactory.initialize(uuid,port);
		
		this.uuid = uuid;
		
		this.subscriber = NetworkServiceFactory.getSubscriber();
		this.fw = fw;
		setupCallback();
	}

	private void setupCallback() {
		logger.info("Starting ContainerPositionUpdate callback" + " container ID: " + container.getID());
		this.callbackPosition = new CallbackContainerPositionUpdate(container, fw);
		subscriber.addSubscription(ContainerPositionUpdate.class, callbackPosition);
		logger.info("Starting CallbackContainerAtFinalDestination callback" + " container ID: " + container.getID());
		this.callbackAtFinal = new CallbackContainerAtFinalDestination(container, this);
		subscriber.addSubscription(ContainerAtFinalDestination.class, callbackAtFinal);
	}


	public Queue<ContainerPositionUpdate> getCallbackQueue() {
		return callbackQueue;
	}

	public Queue<ContainerAtFinalDestination> getCallbackContainerAtFinalDestination() {
		return callbackContainerAtFinalDestination;
	}

	public Location receiveCurentLocation() {
		return null;

	}

	public void requestContainerInformation(Container container) {

	}

	public Subscriber getSubscriber() {
		return subscriber;
	}

	public Publisher getPublisher() {
		return publisher;
	}

	public void close() {
		logger.debug("NetworkServiceFactory terminates all threads");
		NetworkServiceFactory.terminateAllThreads();
	}

}
