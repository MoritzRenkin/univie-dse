package routing;

import map.Location;

public class NeighbourNode {
    private final Location location;
    private final int egdeWeight;

    public NeighbourNode(Location location, int egdeWeight) {
        this.location = location;
        this.egdeWeight = egdeWeight;
    }

    public Location getLocation() {
        return location;
    }

    public int getEgdeWeight() {
        return egdeWeight;
    }
}
