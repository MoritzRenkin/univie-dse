package main.database;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class HubState {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private UUID hubId;
    private double fillingLevel;
    private int capacity;

    public HubState(UUID hubId, double fillingLevel, int capacity) {
        this.hubId = hubId;
        this.fillingLevel = fillingLevel;
        this.capacity = capacity;
    }

    public HubState() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UUID getHubId() {
        return hubId;
    }

    public void setHubId(UUID hubId) {
        this.hubId = hubId;
    }

    public double getFillingLevel() {
        return fillingLevel;
    }

    public void setFillingLevel(double fillingLevel) {
        this.fillingLevel = fillingLevel;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        return "HubState{" +
                "id=" + id +
                ", hubId=" + hubId +
                ", fillingLevel=" + fillingLevel +
                ", capacity=" + capacity +
                '}';
    }
}
