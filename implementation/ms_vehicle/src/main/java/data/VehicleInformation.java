package data;

import java.util.UUID;

public class VehicleInformation {
	private UUID id;
	private UUID motherHubId;
	private VehicleType vehicleType;
	
	public VehicleInformation(UUID id, UUID motherHubId, VehicleType vehicleType) {
		super();
		this.id = id;
		this.motherHubId = motherHubId;
		this.vehicleType = vehicleType;
	}
	public UUID getId() {
		return id;
	}
	public UUID getMotherHubId() {
		return motherHubId;
	}
	public VehicleType getVehicleType() {
		return vehicleType;
	}
}
