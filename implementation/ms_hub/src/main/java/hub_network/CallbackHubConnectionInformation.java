package hub_network;

import map.HubNeighbourInformation;
import map.Location;
import map.LocationConnection;
import map.MS_Type;
import messageUtil.NodeConnection;
import messages.HubConnectionInformation;
import network.Callback;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class CallbackHubConnectionInformation implements Callback<HubConnectionInformation> {
    private final AtomicInteger numberOfHubs;
    private final Queue<HubNeighbourInformation> hubConnectionInfo;


    public CallbackHubConnectionInformation(Queue<HubNeighbourInformation> hubConnectionInfo, AtomicInteger numberOfHubs) {
        this.hubConnectionInfo = hubConnectionInfo;
        this.numberOfHubs = numberOfHubs;
    }

    @Override
    public void onResponse(HubConnectionInformation message) {
        Set<LocationConnection> locationConnections = new HashSet<>();
        for (NodeConnection nodeCon : message.getConnections()) {
            Location location1 = new Location(message.getSenderUUID(), MS_Type.HUB);
            Location location2;
            if (nodeCon.isHub()) {
                location2 = new Location(nodeCon.getNodeId(), MS_Type.HUB);
            } else {
                location2 = new Location(nodeCon.getNodeId(), MS_Type.STATION);
            }
            locationConnections.add(new LocationConnection(location1, location2, nodeCon.getDistance()));
        }
        this.hubConnectionInfo.add(new HubNeighbourInformation(locationConnections));
    }

}

