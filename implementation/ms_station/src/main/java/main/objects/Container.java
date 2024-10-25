package main.objects;

import main.MainController;
import main.properties.Properties;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Container {
    private final UUID id;
    private UUID currentLocation;
    private UUID destinationLocation;
    private int weight;
    private List<UUID> locationList;
    private UUID currentHub;
    private int port;

    public Container(UUID destinationLocation, int weight, int port) {
        this(UUID.randomUUID(),destinationLocation, weight,null,port);
    }
    public Container(UUID id, UUID destinationLocation, int weight, UUID currentHub) {
        this(UUID.randomUUID(),destinationLocation, weight,null,0);
    }

    public Container(UUID id, UUID destinationLocation, int weight, UUID currentHub, int port){
        this.id = id;
        this.destinationLocation = destinationLocation;
        this.weight = weight;
        this.currentLocation = MainController.STATION_ID;
        this.currentHub = currentHub;
        this.locationList = new ArrayList<>();
        this.port = port;
    }


    public UUID getId() {
        return id;
    }

    public UUID getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(UUID currentLocation) {
        this.currentLocation = currentLocation;
    }

    public UUID getDestinationLocation() {
        return destinationLocation;
    }

    public void setDestinationLocation(UUID destinationLocation) {
        this.destinationLocation = destinationLocation;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public UUID getCurrentHub() {
        return currentHub;
    }

    public void setCurrentHub(UUID currentHub) {
        this.currentHub = currentHub;
    }

    public List<UUID> getLocationList() {
        return locationList;
    }

    public void setLocationList(List<UUID> locationList) {
        this.locationList = locationList;
    }

    public int getPort() {
        return port;
    }
}
