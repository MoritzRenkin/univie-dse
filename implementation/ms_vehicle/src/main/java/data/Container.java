package data;

import java.util.UUID;

public class Container {
	private final UUID id;
	private final int weight;
	private final UUID sourceStation;
	private final UUID destinationStation;

	public Container(UUID id, int weight, UUID sourceStation, UUID destinationStation) {
		this.id = id;
		this.weight = weight;
		this.sourceStation = sourceStation;
		this.destinationStation = destinationStation;
	}

	public UUID getId() {
		return id;
	}
	public int getWeight() {
		return weight;
	}

	public UUID getSourceStation() {
		return sourceStation;
	}

	public UUID getDestinationStation() {
		return destinationStation;
	}
}
