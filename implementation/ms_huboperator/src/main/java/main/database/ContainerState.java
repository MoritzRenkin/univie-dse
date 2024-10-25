package main.database;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class ContainerState {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private UUID containerId;
    private UUID currentLocation;
    private UUID vehicleId;
    private double distanceToGo;

    public ContainerState(UUID containerId, UUID currentLocation, UUID vehicleId, double distanceToGo) {
        this.containerId = containerId;
        this.currentLocation = currentLocation;
        this.vehicleId = vehicleId;
        this.distanceToGo = distanceToGo;
    }

    public ContainerState() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UUID getContainerId() {
        return containerId;
    }

    public void setContainerId(UUID containerId) {
        this.containerId = containerId;
    }

    public UUID getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(UUID currentLocation) {
        this.currentLocation = currentLocation;
    }

    public double getDistanceToGo() {
        return distanceToGo;
    }

    public void setDistanceToGo(double distanceToGo) {
        this.distanceToGo = distanceToGo;
    }

    public UUID getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(UUID vehicleId) {
        this.vehicleId = vehicleId;
    }
}
