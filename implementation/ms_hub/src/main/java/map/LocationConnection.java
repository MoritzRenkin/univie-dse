package map;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class LocationConnection {
    private final Set<Location> pathEnds;
    private final int distance;

    public LocationConnection(Location location1, Location location2, int distance) {
        Set<Location> pathEnds = new HashSet<>();
        pathEnds.add(location1);
        pathEnds.add(location2);
        this.pathEnds = pathEnds;
        this.distance = distance;
    }

    public Set<Location> getPathEnds() {
        return pathEnds;
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocationConnection locationConnection = (LocationConnection) o;
        return distance == locationConnection.distance &&
                Objects.equals(pathEnds, locationConnection.pathEnds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pathEnds, distance);
    }

    @Override
    public String toString() {
        return "LocationConnection{" +
                "pathEnds=" + pathEnds +
                ", distance=" + distance +
                '}';
    }
}
