package main.database;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class ContainerHistoryState {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private UUID containerId;
    private UUID locationId;

    public ContainerHistoryState(UUID containerId, UUID locationId) {
        this.containerId = containerId;
        this.locationId = locationId;
    }

    public ContainerHistoryState() {
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public UUID getContainerId() {
        return containerId;
    }

    public void setContainerId(UUID containerId) {
        this.containerId = containerId;
    }

    public UUID getLocationId() {
        return locationId;
    }

    public void setLocationId(UUID locationId) {
        this.locationId = locationId;
    }
}
