package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import container.Container;
import container.Location;
import container.LocationHistory;
import network.NetworkController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Controller {
	
	 
	private UUID uuid;
	private int port;
	private Container container;
	private NetworkController networkController;
	private static Logger logger = LoggerFactory.getLogger(Controller.class);
	private FileWriter fw;
	private Location source;


	public Controller(UUID uuid, int weight, int port, UUID current, UUID destination) {
		
		this.uuid = uuid;
		this.port = port;


		Location currentLocation = new Location(current);
		source = currentLocation;
		Location destinationLocation = new Location(destination);
		
		logger.debug("Current location: " + currentLocation.getID().toString() + " driving to: " + destinationLocation.getID().toString() + " container ID: " + uuid.toString());
		
		List<Location> li = new ArrayList<>();
		li.add(currentLocation);
		LocationHistory locationHistory = new LocationHistory(li);
		
		this.container = new Container(currentLocation, destinationLocation, weight, uuid, locationHistory);
		logger.debug("Container created" + " container ID: " + container.getID());
		
		try {
			logger.debug("Creating file: " + container.getID().toString());
			this.fw = new FileWriter("ContainerUUID_" + container.getID().toString() + ".txt", false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void startMicroService() {

		logger.debug("Creating NetworkController" + " container ID: " + container.getID());
		this.networkController = new NetworkController(uuid, port, container,fw);

		
		start_network();

	}

	public void start_network() {
		try {
			logger.debug("Writing initial information: " + container.getID().toString());
			fw.write("Source: " + source.getID().toString() + "\n" + "Destination: " + container.getDestinationLocation().getID().toString() + "\n");
			fw.flush();
			fw.write("----------------------------------------------------------------------------" + "\n" );
			fw.flush();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		

		while(true) {
			
			 try {
				Thread.sleep(3000);
				/*
				logger.debug("Writing location in file: " + container.getID().toString());
				fw.write(container.getCurrentLocation().getID().toString() +  "\n");
				fw.flush();
			*/
			} catch (InterruptedException e) {
				logger.error(e.toString());
				System.exit(1);
				e.printStackTrace();
			}
		}

	}


	public NetworkController getNetworkController() {
		return networkController;
	}
	
	
}
