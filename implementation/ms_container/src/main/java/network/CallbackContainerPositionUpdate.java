package network;

import java.io.FileWriter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import container.Container;
import container.Location;
import main.Controller;
import messages.ContainerPositionUpdate;

public class CallbackContainerPositionUpdate implements Callback<ContainerPositionUpdate> {

	private Container container;
	private NetworkController networkController;
	private static Logger logger = LoggerFactory.getLogger(CallbackContainerPositionUpdate.class);
	private FileWriter fw;

	public CallbackContainerPositionUpdate(Container container, FileWriter fw) {
		super();
		this.container = container;
		this.fw = fw;
	}

	@Override
	public void onResponse(ContainerPositionUpdate message) {

		if (container.getID().equals(message.getContainerInformation().getContainerId())) {
			Location loc = new Location(message.getContainerInformation().getCurrentHub());
			container.setCurrentLocation(loc);
			logger.debug("New location set: "  + loc.getID() + " container ID: " + container.getID());
			
			if (!container.getLocationHistory().getLocationHistory()
					.get(container.getLocationHistory().getLocationHistory().size() - 1).getID().equals(loc.getID())) {
				// TODO properly, ELSE
				container.getLocationHistory().addLocation(loc);
				try {
					fw.write(container.getCurrentLocation().getID().toString() +  "\n");
					fw.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				logger.debug("Added to Local History: "  + loc.getID() + " container ID: " + container.getID() );
			}else {
				logger.debug("local history fail " + container.getID());
			}
		}else {
			logger.debug("adding failed " + container.getID());
		}
		if(container.getCurrentLocation().getID().equals(container.getDestinationLocation().getID())) {
			logger.debug("Container at destination, container ID: " + container.getID());
			networkController.close();
		}

	}
}
