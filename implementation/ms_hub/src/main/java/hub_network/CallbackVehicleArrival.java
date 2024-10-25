package hub_network;

import messageUtil.ContainerInformation;
import messages.VehicleArrival;
import network.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import transport.Container;
import transport.Vehicle;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

public class CallbackVehicleArrival implements Callback<VehicleArrival> {
    private final UUID myUUID;
    private final Set<Vehicle> assignedVehicles;

    Queue<UUID> idleVehicleArrivalQueue;
    Queue<Vehicle> myFullVehicleArrivalQueue;
    Queue<Vehicle> foreignFullVehicleArrivalQueue;

    private static final Logger logger = LoggerFactory.getLogger(CallbackVehicleArrival.class);

    public CallbackVehicleArrival(UUID myUUID, Set<Vehicle> assignedVehicles, Queue<UUID> idleV, Queue<Vehicle> fullMyV, Queue<Vehicle> foreignFullV) {
        this.myUUID = myUUID;
        this.assignedVehicles = assignedVehicles;
        this.idleVehicleArrivalQueue = idleV;
        this.myFullVehicleArrivalQueue = fullMyV;
        this.foreignFullVehicleArrivalQueue = foreignFullV;
    }

    @Override
    public void onResponse(VehicleArrival message) {
        if (message.getTargetHub().equals(myUUID)) {
            if (message.getContainersContained().isEmpty()) {
                logger.debug("idle vehicle arrived");
                idleVehicleArrivalQueue.add(message.getSenderUUID());
            } else {
                Set<Container> containersContained = new HashSet<>();
                for (ContainerInformation cont : message.getContainersContained()) {
                    containersContained.add(new Container(cont.getContainerId(), cont.getWeight(), cont.getSourceStation(), cont.getDestinationStation()));
                }
                Vehicle vehicle = new Vehicle(message.getSenderUUID(), containersContained);
                if (assignedVehicles.contains(vehicle)) {
                    logger.debug("my full vehicle arrived");
                    myFullVehicleArrivalQueue.add(vehicle);
                } else {
                    logger.debug("foreign full vehicle arrived");
                    foreignFullVehicleArrivalQueue.add(vehicle);
                }
            }
        }
    }
}
