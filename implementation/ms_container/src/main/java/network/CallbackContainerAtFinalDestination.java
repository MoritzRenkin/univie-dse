package network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import container.Container;
import messages.ContainerAtFinalDestination;
import messages.ContainerPositionUpdate;

public class CallbackContainerAtFinalDestination implements Callback<ContainerAtFinalDestination>{
	
	private NetworkController networkController;
	private Container container;
	private static Logger logger = LoggerFactory.getLogger(CallbackContainerAtFinalDestination.class);
	
	public CallbackContainerAtFinalDestination(Container container,NetworkController networkController) {
		super();
		this.networkController = networkController;
		this.container = container;
	}

	@Override
	public void onResponse(ContainerAtFinalDestination message) {
		if(container.getID().equals(message.getContainerInformation().getContainerId())) {
			logger.info("Container at final destination, terminate all threads container ID: " + container.getID());
			networkController.close();
		}else {
			logger.info("ContainerAtFinalDestination else" + container.getID());
		}
		
	}

}
