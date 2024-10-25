package messages;

import messageUtil.ContainerInformation;

import java.util.Collection;
import java.util.UUID;

/**
 * Sent out be vehicle to signal arrival at a source node to pick up certain containers.
 */
public class ContainerPickUpRequest extends AbstractMessage {


    private static final long serialVersionUID = -6928225057981933397L;
    private final UUID vehicleId;
    private final UUID stationId;
    private final Collection<ContainerInformation> containersToHandover;

    public ContainerPickUpRequest(UUID senderUUID, UUID stationId, Collection<ContainerInformation> containersToHandover) {
        super(senderUUID);
        this.vehicleId = senderUUID;
        this.stationId = stationId;
        this.containersToHandover = containersToHandover;
    }

    public UUID getVehicleId() {
        return vehicleId;
    }

    public UUID getStationId() {
        return stationId;
    }

    public Collection<ContainerInformation> getContainersToHandover() {
        return containersToHandover;
    }

    @Override
    public String toString() {
        return "ContainerPickUpRequest{" +
                "messageUUID=" + getMessageUUID() +
                ", senderUUID=" + getSenderUUID() +
                ", vehicleId=" + vehicleId +
                ", stationId=" + stationId +
                ", containersToHandover=" + containersToHandover +
                '}';
    }
}
