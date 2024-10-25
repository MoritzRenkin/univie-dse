package routing;

import map.Location;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class MainNode {
    private final Location location;
    private int distance; //for dijsktra
    private List<MainNode> formerNode; //max 1 element

    public MainNode(Location location) {
        this.location  = location;
        this.distance = Integer.MAX_VALUE;
        this.formerNode = new LinkedList<>();
    }

    public MainNode(Location location, int distance) {
        this.location = location;
        this.distance = distance;
        this.formerNode = new LinkedList<>();

    }

    public Location getLocation() {
        return location;
    }

    public int getDistance() {
        return distance;
    }

    public List<MainNode> getFormerNode() {
        return formerNode;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void setFormerNode(List<MainNode> formerNode) {
        this.formerNode = formerNode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MainNode mainNode = (MainNode) o;
        return Objects.equals(location, mainNode.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(location);
    }
}
