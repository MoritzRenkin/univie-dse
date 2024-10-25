package container;

import java.util.UUID;

public class Location {
	private UUID ID;

	public Location(UUID ID) {
		this.ID = ID;
	}

	public UUID getID() {
		return ID;
	}
}
