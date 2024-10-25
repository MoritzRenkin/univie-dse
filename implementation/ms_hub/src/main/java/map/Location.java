package map;

import java.util.Objects;
import java.util.UUID;

public class Location {
    private final UUID ID;
    private final MS_Type type;
    private String name;

    public Location(UUID id, MS_Type type) {
        ID = id;
        this.type = type;
    }

    public Location(UUID id, MS_Type type, String name) {
        ID = id;
        this.type = type;
        this.name = name;
    }

    public UUID getID() {
        return ID;
    }

    public MS_Type getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Objects.equals(ID, location.ID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID);
    }

    @Override
    public String toString() {
        return "Location{" +
                "ID=" + ID +
                ", type=" + type +
                ", name='" + name + '\'' +
                '}';
    }
}
