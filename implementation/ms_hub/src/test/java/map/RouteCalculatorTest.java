package map;

import org.junit.Test;
import routing.MainNode;
import routing.RouteCalculator;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RouteCalculatorTest {
    private static final Location myLocation = new Location(UUID.fromString("7b11af48-d117-4325-b957-75f9ab87ca4d"), MS_Type.HUB, "myHub");
    //my hub connections:
    private static final Location station1 = new Location(UUID.fromString("441e16c8-4492-11eb-b378-0242ac130002"), MS_Type.STATION, "station1");
    private static final Location station2 = new Location(UUID.fromString("441e18e4-4492-11eb-b378-0242ac130002"), MS_Type.STATION, "station2");
    private static final Location hub3 = new Location(UUID.fromString("441e1b32-4492-11eb-b378-0242ac130002"), MS_Type.HUB, "hub3");
    //hub2 connections:
    //hub3
    private static final Location station5 = new Location(UUID.fromString("441e1c18-4492-11eb-b378-0242ac130002"), MS_Type.STATION, "station5");
    private static final Location station4 = new Location(UUID.fromString("441e1ce0-4492-11eb-b378-0242ac130002"), MS_Type.STATION, "station4");
    //hub3 connections:
    // my hub
    //station 5
    private static final Location hub2 = new Location(UUID.fromString("441e1d9e-4492-11eb-b378-0242ac130002"), MS_Type.HUB, "hub2");
    private final RouteCalculator routeCalculator;
    private final MapManager mapManager;

    public RouteCalculatorTest() {
        Set<LocationConnection> neighbourConnections = new HashSet<>();
        neighbourConnections.add(new LocationConnection(myLocation, station1, 4));
        neighbourConnections.add(new LocationConnection(myLocation, station2, 5));
        neighbourConnections.add(new LocationConnection(myLocation, hub3, 7));
        mapManager = new MapManager(myLocation.getID(), neighbourConnections);

        Queue<HubNeighbourInformation> connectionInfo = new LinkedList<>();

        Set<LocationConnection> hub2ConnectedNodes = new HashSet<>();
        hub2ConnectedNodes.add(new LocationConnection(hub2, hub3, 10));
        hub2ConnectedNodes.add(new LocationConnection(hub2, station4, 4));
        hub2ConnectedNodes.add(new LocationConnection(hub2, station5, 6));
        connectionInfo.add(new HubNeighbourInformation(hub2ConnectedNodes));

        Set<LocationConnection> hub3ConnectedNodes = new HashSet<>();
        hub3ConnectedNodes.add(new LocationConnection(hub3, hub2, 10));
        hub3ConnectedNodes.add(new LocationConnection(hub3, myLocation, 7));
        hub3ConnectedNodes.add(new LocationConnection(hub3, station5, 7));
        connectionInfo.add(new HubNeighbourInformation(hub3ConnectedNodes));

        mapManager.addHubNeighbourConnectionsToMap(connectionInfo);
        routeCalculator = new RouteCalculator();
        routeCalculator.setMap(mapManager.getMap());
    }

    @Test
    public void generatedGraph_shouldContainAllHubsAndSourceAndDestination() {
        Location startingLocation = station1;
        Location destinationLocation = station4;
        routeCalculator.calculateRoute(startingLocation, destinationLocation);
        assertTrue(routeCalculator.getGraph().containsKey(new MainNode(startingLocation)));
        assertTrue(routeCalculator.getGraph().containsKey(new MainNode(destinationLocation)));
        assertTrue(routeCalculator.getGraph().containsKey(new MainNode(myLocation)));
        assertTrue(routeCalculator.getGraph().containsKey(new MainNode(hub3)));
        assertTrue(routeCalculator.getGraph().containsKey(new MainNode(hub3)));
    }

    @Test
    public void calculatedRoute_shouldContainSourceDestinationAndHubsInTheRoute() {
        Location startingLocation = station1;
        Location destinationLocation = station4;
        routeCalculator.calculateRoute(startingLocation, destinationLocation);
        List<Location> route = routeCalculator.calculateRoute(startingLocation, destinationLocation);
        assertThat(route.get(0), is(equalTo(startingLocation)));
        assertThat(route.get(route.size() - 1), is(equalTo(destinationLocation)));
        for (int i = 1; i < route.size() - 1; i++) {
            assertTrue(route.get(i).getType().equals(MS_Type.HUB));
        }
    }

}
