package container;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import network.CallbackContainerAtFinalDestination;

public class Container {
	private Location currentLocation;
	private Location destinationLocation;
	private int weight;
	private UUID uuid;
	private LocationHistory LocationHistory;
	private static Logger logger = LoggerFactory.getLogger(Container.class);

	public Container(Location currentLocation, Location destinationLocation, int weight, UUID uuid,
			LocationHistory locationHistory) {
		this.currentLocation = currentLocation;
		this.destinationLocation = destinationLocation;
		this.weight = weight;
		this.LocationHistory = locationHistory;
		this.uuid = uuid;
	}

	public Location getCurrentLocation() {
		return currentLocation;
	}

	public Location getDestinationLocation() {
		return destinationLocation;
	}

	public int getWeight() {
		return weight;
	}

	public UUID getID() {
		return uuid;
	}

	public LocationHistory getLocationHistory() {
		return LocationHistory;
	}

	public void setCurrentLocation(Location currentLocation) {
		logger.debug("Setting new location, container ID: " + uuid);
		this.currentLocation = currentLocation;
	}
	
}
