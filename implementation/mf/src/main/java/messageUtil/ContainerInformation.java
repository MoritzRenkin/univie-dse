package messageUtil;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class ContainerInformation implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 8710584241603209070L;
	private UUID containerId;
	private int weight;
	private UUID sourceStation;
	private UUID destinationStation;
	private UUID currentHub;
		

	public ContainerInformation(UUID containerId, int weight, UUID sourceStation, UUID destinationStation,
			UUID currentHub) {
		super();
		this.containerId = containerId;
		this.weight = weight;
		this.sourceStation = sourceStation;
		this.destinationStation = destinationStation;
		this.currentHub = currentHub;
	}



	public UUID getContainerId() {
		return containerId;
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

	public UUID getCurrentHub() {
		return currentHub;
	}



	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ContainerInformation)) return false;
		ContainerInformation that = (ContainerInformation) o;
		return weight == that.weight && containerId.equals(that.containerId) && sourceStation.equals(that.sourceStation) && destinationStation.equals(that.destinationStation);
	}

	@Override
	public int hashCode() {
		return Objects.hash(containerId, weight, sourceStation, destinationStation);
	}
}
