package jsonConvert;

import map.LocationConnection;
import transport.Vehicle;

import java.util.Set;

public class StartUpInfo {
    private final Set<LocationConnection> neighbourConnections;
    private final Set<Vehicle> assignedVehicles;
    private final int hubCapacity;
    private final int numberOfHubs;

    public StartUpInfo(Set<LocationConnection> neighbourConnections, Set<Vehicle> assignedVehicles, int hubCapacity, int numberOfHubs) {
        this.neighbourConnections = neighbourConnections;
        this.assignedVehicles = assignedVehicles;
        this.hubCapacity = hubCapacity;
        this.numberOfHubs = numberOfHubs;
    }

    public Set<LocationConnection> getNeighbourConnections() {
        return neighbourConnections;
    }

    public Set<Vehicle> getAssignedVehicles() {
        return assignedVehicles;
    }

    public int getHubCapacity() {
        return hubCapacity;
    }

    public int getNumberOfHubs() {
        return numberOfHubs;
    }
}
