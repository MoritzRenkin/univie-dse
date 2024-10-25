package main;


import network.NetworkServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class Main {
	private static Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {

		UUID vehicleId = UUID.fromString(args[0]);
		
		Controller controller = new Controller(vehicleId);
		
		try {
			controller.startMicroService();
			
		} catch (Exception e) {
			logger.error("Uncaught exception", e);

		}
		finally {
			try {
				NetworkServiceFactory.terminate();
			} catch (Exception ignored) {}
			System.exit(1);
		}
	}

}
