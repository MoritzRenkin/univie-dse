package map;

import java.util.HashSet;
import java.util.Set;

public class Map {
    private Set<LocationConnection> locationConnections;
    private Set<Location> locations;

    public Map(Set<LocationConnection> locationConnections, Set<Location> locations) {
        this.locationConnections = locationConnections;
        this.locations = locations;
    }

    public Set<LocationConnection> getLocationConnections() {
        return locationConnections;
    }

    public Set<Location> getLocations() {
        return locations;
    }

    public void addPaths (Set<LocationConnection> neighbourLocationConnections) {
        locationConnections.addAll(neighbourLocationConnections);
    }

    public void addLocation (Location location) {
        locations.add(location);
    }

    public Set<Location> getNeighboursForSpecificLocation(Location location) {
        Set<Location> neighbours = new HashSet<>();
        for (LocationConnection locationConnection : locationConnections) {
            if (locationConnection.getPathEnds().contains(location)) {
                for (Location neighbourLocation : locationConnection.getPathEnds()) {
                    if (!neighbourLocation.equals(location)) {
                        neighbours.add(neighbourLocation);
                    }
                }
            }
        }
        return neighbours;
    }
}
