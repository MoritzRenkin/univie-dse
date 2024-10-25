package transport;

import map.Location;
import map.MS_Type;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;

public class Container {

    private final UUID ID;
    private final int weight;
    private final Location destination;
    private final Location sourceStation;
    private boolean fromPickUpOrder;

    private Timestamp addedToStorageTime;

    public Container(UUID id, int weight, UUID sourceStation, UUID destination) {
        this.ID = id;
        this.weight = weight;
        this.destination = new Location(destination, MS_Type.STATION);
        this.sourceStation = new Location(sourceStation, MS_Type.STATION);
        this.addedToStorageTime = new Timestamp(System.currentTimeMillis());
        this.fromPickUpOrder = false;
    }


    public UUID getID() {
        return ID;
    }

    public int getWeight() {
        return weight;
    }

    public Location getDestination() {
        return destination;
    }

    public Location getSourceStation() {
        return sourceStation;
    }

    public long getInStorageTime() { //in ms
        return System.currentTimeMillis()- addedToStorageTime.getTime();
    }

    public long getAddedToStorageTime() {
        return addedToStorageTime.getTime();
    }

    public void setFromPickUpOrder(boolean fromPickUpOrder) {
        this.fromPickUpOrder = fromPickUpOrder;
    }

    public boolean isFromPickUpOrder() {
        return fromPickUpOrder;
    }

    public void resetStorageTime() {
        addedToStorageTime = new Timestamp(System.currentTimeMillis());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Container container = (Container) o;
        return Objects.equals(ID, container.ID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID);
    }

    @Override
    public String toString() {
        return "Container{" +
                "ID=" + ID +
                ", weight=" + weight +
                ", destination=" + destination +
                ", sourceStation=" + sourceStation +
                ", fromPickUpOrder=" + fromPickUpOrder +
                ", addedToStorageTime=" + addedToStorageTime +
                '}';
    }
}
