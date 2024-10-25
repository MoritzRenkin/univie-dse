package routing;

import map.Location;
import map.LocationConnection;
import map.MS_Type;
import map.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


/**
 * this class is used for calculating the path from a given source to a given destination using dijkstra
 * a graph which only contains source/destination and hubs is used
 **/
public class RouteCalculator {

    private static final Logger logger = LoggerFactory.getLogger(RouteCalculator.class);

    private Map map;
    private Location destinationLocation;
    private Location startingLocation;
    private final HashMap<MainNode, List<NeighbourNode>> graph;

    public RouteCalculator() {
        super();
        this.graph = new HashMap<>();
    }

    public List<Location> calculateRoute(Location startingLocation, Location destinationLocation) {
        if (!map.getLocations().contains(startingLocation)) {
            throw new IllegalArgumentException("No such starting Location in map, cannot start routing");
        } else if (!map.getLocations().contains(destinationLocation)) {
            throw new IllegalArgumentException("No such destination Location in map, cannot start routing");
        }

        initializeCalculator(destinationLocation, startingLocation);
        generateGraph();
        doDijkstraAlgorithm();

        List<Location> shortestPath = new ArrayList<>();
        shortestPath.add(destinationLocation);
        Location lastLocation = destinationLocation;
        boolean finished = false;
        while (!finished) {
            for (MainNode node : graph.keySet()) {
                if (node.getLocation().equals(lastLocation)) {
                    if (!node.getFormerNode().isEmpty()) {
                        shortestPath.add(0, node.getFormerNode().get(0).getLocation());
                        lastLocation = node.getFormerNode().get(0).getLocation();
                    } else {
                        finished = true;
                    }
                    break;
                }
            }
        }
        logger.debug("shortest Path by route Calculator (source: {}, destination: {})", startingLocation.toString(), destinationLocation.toString());
        for (Location l : shortestPath) {
            logger.debug(l.toString());
        }
        return shortestPath;
    }

    private void initializeCalculator(Location destinationLocation, Location startingLocation) {
        this.destinationLocation = destinationLocation;
        this.startingLocation = startingLocation;
    }

    private void generateGraph() {
        //sets and generates the graph which contains only source, destination and hubs
        graph.clear();
        for (Location location : map.getLocations()) {
            List<NeighbourNode> neighbourLocations = new LinkedList<>();
            if (location.getType().equals(MS_Type.HUB) || location.equals(destinationLocation) || location.equals(startingLocation)) {
                for (Location neighbour : map.getNeighboursForSpecificLocation(location)) {
                    try {
                        neighbourLocations.add(new NeighbourNode(neighbour, getEdgeWeight(location, neighbour)));
                    } catch (Exception e) {
                        logger.error("invalid location connection", e);
                    }
                }
                if (location.equals(startingLocation)) {
                    graph.put(new MainNode(location, 0), neighbourLocations);
                } else {
                    graph.put(new MainNode(location), neighbourLocations);
                }
            }
        }

    }

    private int getEdgeWeight(Location location, Location neighbour) {
        //returns distance between two nodes in a path
        for (LocationConnection locationConnection : map.getLocationConnections()) {
            if (locationConnection.getPathEnds().contains(location) && locationConnection.getPathEnds().contains(neighbour)) {
                return locationConnection.getDistance();
            }
        }
        throw new IllegalArgumentException("path does not exist in the map");
    }

    private void doDijkstraAlgorithm() {
        HashMap<MainNode, List<NeighbourNode>> graphCopy = graph;
        List<MainNode> graphCopyKeyList = new LinkedList<>(graphCopy.keySet());
        while(!graphCopyKeyList.isEmpty()) {
            MainNode min = getMinimumDistanceNode(graphCopyKeyList);
            graphCopyKeyList.remove(min);
            List<NeighbourNode> neighbours = graph.get(min);
            List<Location> graphCopyKeyListLocations = new ArrayList<>();
            for (MainNode mainNode : graphCopyKeyList) {
                graphCopyKeyListLocations.add(mainNode.getLocation());
            }
            for (NeighbourNode neighbour : neighbours) {
                if (graphCopyKeyListLocations.contains(neighbour.getLocation())) {
                    changeNodeDistance(neighbour,min);
                }
            }
            if (min.getLocation().equals(destinationLocation)) {
                return;
            }
        }
    }

    private MainNode getMinimumDistanceNode(List<MainNode> graphCopyKeyList) {
        MainNode min = null;
        for (MainNode nodeCopy : graphCopyKeyList) {
            for (MainNode original : graph.keySet()) {
                if(original.equals(nodeCopy)) {
                    if (min == null) {
                        min = original;
                    } else if (original.getDistance() < min.getDistance()) {
                        min = original;
                    }
                }
            }
        }
        return min;
    }

    private void changeNodeDistance(NeighbourNode neighbourNode, MainNode mainNode) {
        for (MainNode toSetFormer : graph.keySet()) {
            if (neighbourNode.getLocation().equals(toSetFormer.getLocation())) {
                int distanceSum = mainNode.getDistance() + neighbourNode.getEgdeWeight();
                if (distanceSum < toSetFormer.getDistance()) {
                    toSetFormer.setDistance(distanceSum);
                    List<MainNode> formerNode = new LinkedList<>();
                    formerNode.add(mainNode);
                    toSetFormer.setFormerNode(formerNode);
                }
            }
        }
    }

    public HashMap<MainNode, List<NeighbourNode>> getGraph() {
        return graph;
    }

    public void setMap(Map map) {
        this.map = map;
    }
}
