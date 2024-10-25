package map;

import java.util.Set;

/**
 * used to convert from HubConnectionInformation in MF
 */

public class HubNeighbourInformation {
    private final Set<LocationConnection> neighbourConnections;


    public HubNeighbourInformation(Set<LocationConnection> neighbourConnections) {
        this.neighbourConnections = neighbourConnections;
    }

    public Set<LocationConnection> getNeighbourConnections() {
        return neighbourConnections;
    }

    @Override
    public String toString() {
        return "HubNeighbourInformation{" +
                "neighbourConnections=" + neighbourConnections +
                '}';
    }
}
