package transport;

import java.util.*;
import java.util.stream.Collectors;

public class VehicleManager {
    private final Set<Vehicle> storedVehicles;

    public VehicleManager(Set<Vehicle> vehicles) {
        this.storedVehicles = vehicles;
    }

    public void addVehicleBackToStorage(UUID vehicleId) {
        for (Vehicle vehicle : storedVehicles) {
            if (vehicle.getVehicleID().equals(vehicleId)) {
                vehicle.setVehicleTransportState(EVehicleTransportState.INSTORAGE);
                vehicle.removeAllContainers();
            }
        }
    }

    public Optional<Vehicle> getFittingVehicle(int capacity) {
        //finds a vehicle which fits the capacity best
        Set<Vehicle> availableVehicles = getAvailableVehicles();
        if (availableVehicles.isEmpty()) {
            return Optional.empty();
        }

        Set<Vehicle> fittingVehicles = new HashSet<>();
        for (Vehicle vehicle : availableVehicles) {
            if (vehicle.getVehicleCapacity() >= capacity) {
                fittingVehicles.add(vehicle);
            }
        }
        Optional<Vehicle> returningVehicle;
        if (fittingVehicles.isEmpty()) {
            //all vehicles are too small
            returningVehicle = availableVehicles
                    .stream()
                    .max(Comparator.comparing(Vehicle::getVehicleCapacity));
        } else {
           returningVehicle = fittingVehicles
                   .stream()
                   .min(Comparator.comparing(Vehicle::getVehicleCapacity));
        }
        for (Vehicle vehicle : storedVehicles) {
            if (vehicle.equals(returningVehicle.get())) {
                return Optional.of(vehicle);
            }
        }
        return Optional.empty();
    }

    public Set<Vehicle> getAvailableVehicles() {
        Set<Vehicle> availableVehicles = new HashSet<>();
        storedVehicles.stream()
                .filter(v -> v.getVehicleTransportState().equals(EVehicleTransportState.INSTORAGE))
                .collect(Collectors.toCollection(() -> availableVehicles));
        return availableVehicles;
    }

    public Set<Vehicle> getStoredVehicles() {
        return storedVehicles;
    }
}
