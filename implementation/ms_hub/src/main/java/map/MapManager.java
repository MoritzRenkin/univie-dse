package map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import routing.RouteCalculator;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

public class MapManager {
    private static final Logger logger = LoggerFactory.getLogger(MapManager.class);
    private final RouteCalculator routeCalculator;
    private final Location myLocation;
    private final Map map;

    public MapManager(UUID myUUID, Set<LocationConnection> neighbourConnections) {
        this.myLocation = new Location(myUUID, MS_Type.HUB, "myHub");
        Set<Location> mapLocations = new HashSet<>();
        mapLocations.add(myLocation);
        for (LocationConnection neighbourLocationConnection : neighbourConnections) {
            for (Location pathEnd : neighbourLocationConnection.getPathEnds()) {
                if (!pathEnd.getID().equals(myUUID)) {
                    mapLocations.add(pathEnd);
                }
            }
        }
        this.map = new Map(neighbourConnections, mapLocations);
        this.routeCalculator = new RouteCalculator();
    }

    public void addHubNeighbourConnectionsToMap(Queue<HubNeighbourInformation> connectionInformation) {
        while(!connectionInformation.isEmpty()) {
            HubNeighbourInformation polledInformation = connectionInformation.poll();
            for (LocationConnection connection : polledInformation.getNeighbourConnections()) {
                for (Location location : connection.getPathEnds()) {
                    map.addLocation(location);
                }
            }
            map.addPaths(polledInformation.getNeighbourConnections());
            logger.debug("added path:" + polledInformation.getNeighbourConnections());
        }
        routeCalculator.setMap(this.map);
    }

    public Location getNextHop(Location startingLocation, Location destination) {
        Location nextHop = null;
        try {
            nextHop = routeCalculator.calculateRoute(startingLocation, destination).get(1);
        } catch (Exception e) {
            logger.error("routing calc exception: ", e);
        }
        return nextHop;
    }

    public Location getNextHop(Location destination) {
        Location nextHop = null;
        try {
            nextHop = routeCalculator.calculateRoute(myLocation, destination).get(1);
        } catch (Exception e) {
            logger.error("routing calc exception: ", e);
        }
        return nextHop;
    }

    public int getDistance(Location location) {
        //returns distance between two nodes in a path
        for (LocationConnection locationConnection : map.getLocationConnections()) {
            if (locationConnection.getPathEnds().contains(location) && locationConnection.getPathEnds().contains(myLocation)) {
                return locationConnection.getDistance();
            }
        }
        throw new IllegalArgumentException("path does not exist in the map");
    }

    public Map getMap() {
        return map;
    }
}
