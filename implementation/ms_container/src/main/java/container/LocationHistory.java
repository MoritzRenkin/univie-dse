package container;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocationHistory {
	private List<Location> locationHistory;
	private static Logger logger = LoggerFactory.getLogger(LocationHistory.class);

	public LocationHistory(List<Location> locationHistory) {
		this.locationHistory = locationHistory;
	}

	public List<Location> getLocationHistory() {
		return locationHistory;
	}

	public void addLocation(Location location) {
		logger.debug("Added new location to localHistory");
		locationHistory.add(location);
	}

	public int size() {
		return locationHistory.size();
	}
}
