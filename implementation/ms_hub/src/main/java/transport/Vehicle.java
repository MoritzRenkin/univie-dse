package transport;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class Vehicle {
    private final UUID vehicleID;
    private Set<Container> storedContainers;
    private EVehicleTransportState vehicleTransportState; //pick up or deliver container, info for networkController
    private EVehicleType vehicleType;


    public Vehicle(UUID vehicleID) {
        this.vehicleID = vehicleID;
        this.vehicleTransportState = EVehicleTransportState.INSTORAGE;
        this.storedContainers = new HashSet<>();
    }

    public Vehicle(UUID vehicleID, Set<Container> storedContainers) {
        this.vehicleID = vehicleID;
        this.storedContainers = storedContainers;
    }

    public void removeAllContainers() {
        storedContainers.clear();
    }


    public UUID getVehicleID() {
        return vehicleID;
    }

    public EVehicleTransportState getVehicleTransportState() {
        return vehicleTransportState;
    }

    public int getVehicleCapacity() {
        return vehicleType.getCapacity();
    }

    public void setVehicleTransportState(EVehicleTransportState vehicleTransportState) {
        this.vehicleTransportState = vehicleTransportState;
    }

    public Set<Container> getStoredContainers() {
        return storedContainers;
    }

    public void setVehicleType(EVehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehicle vehicle = (Vehicle) o;
        return Objects.equals(vehicleID, vehicle.vehicleID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vehicleID);
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "vehicleID=" + vehicleID +
                ", vehicleTransportState=" + vehicleTransportState +
                ", vehicleType=" + vehicleType +
                '}';
    }
}
