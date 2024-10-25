package map;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class MapManagerTest {
    private MapManager mapManager;
    private static Location myLocation = new Location(UUID.fromString("7b11af48-d117-4325-b957-75f9ab87ca4d"),MS_Type.HUB);
    //my hub connections:
    private static String station1 = "441e16c8-4492-11eb-b378-0242ac130002";
    private static String station2 = "441e18e4-4492-11eb-b378-0242ac130002";
    private static String hub3 = "441e1b32-4492-11eb-b378-0242ac130002";

    //hub2 connections:
    //hub3
    private static String station5 = "441e1c18-4492-11eb-b378-0242ac130002";
    private static String station4 = "441e1ce0-4492-11eb-b378-0242ac130002";

    //hub3 connections:
    // my hub
    //station 5
    private static String hub2 = "441e1d9e-4492-11eb-b378-0242ac130002";

    public MapManagerTest() {
        Set<LocationConnection> neighbourConnections = new HashSet<>();
        neighbourConnections.add(new LocationConnection(myLocation, new Location(UUID.fromString(station1),MS_Type.STATION),4));
        neighbourConnections.add(new LocationConnection(myLocation, new Location(UUID.fromString(station2),MS_Type.STATION),5));
        neighbourConnections.add(new LocationConnection(myLocation, new Location(UUID.fromString(hub3),MS_Type.HUB),7));
        mapManager = new MapManager(myLocation.getID(),neighbourConnections);
    }

    @Test
    public void constructorInMapManager_shouldAddAllPaths() {
        assertThat(mapManager.getMap().getLocations().size(), is(equalTo(4)));
    }

    @Test
    public void initializedMap_shouldContainAllPaths() {
        Queue<HubNeighbourInformation> connectionInfo = new LinkedList<>();

        Set<LocationConnection> hub2ConnectedNodes = new HashSet<>();
        hub2ConnectedNodes.add(new LocationConnection(new Location(UUID.fromString(hub2),MS_Type.HUB), new Location(UUID.fromString(hub3),MS_Type.HUB), 10));
        hub2ConnectedNodes.add(new LocationConnection(new Location(UUID.fromString(hub2),MS_Type.HUB), new Location(UUID.fromString(station4),MS_Type.STATION), 4));
        hub2ConnectedNodes.add(new LocationConnection(new Location(UUID.fromString(hub2),MS_Type.HUB), new Location(UUID.fromString(station5),MS_Type.STATION), 6));
        connectionInfo.add(new HubNeighbourInformation(hub2ConnectedNodes));

        Set<LocationConnection> hub3ConnectedNodes = new HashSet<>();
        hub3ConnectedNodes.add(new LocationConnection(new Location(UUID.fromString(hub3),MS_Type.HUB), new Location(UUID.fromString(hub2),MS_Type.HUB), 10));
        hub3ConnectedNodes.add(new LocationConnection(new Location(UUID.fromString(hub3),MS_Type.HUB), myLocation, 7));
        hub3ConnectedNodes.add(new LocationConnection(new Location(UUID.fromString(hub3),MS_Type.HUB), new Location(UUID.fromString(station5),MS_Type.STATION), 7));
        connectionInfo.add(new HubNeighbourInformation(hub3ConnectedNodes));

        mapManager.addHubNeighbourConnectionsToMap(connectionInfo);

        assertThat(mapManager.getMap().getLocationConnections().size(), is(equalTo(7)));
        assertThat(mapManager.getMap().getLocations().size(), is(equalTo(7)));
    }
}
