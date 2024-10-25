package states;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import data.Container;
import data.Converter;
import messageUtil.ContainerInformation;
import messages.ContainerPositionUpdate;
import messages.VehiclePositionUpdate;

public class JourneyState extends State {
	private final UUID source;
	private final UUID destination;
	
	private final Collection<Container> containersContained;
	private final boolean pickup;
	
	private DistanceCalculator distanceCalculator;
	
	private JourneyState(Builder builder) { // use Builder!
		super();
		this.source = builder.source;
		this.destination = builder.destination;
		this.containersContained = builder.containersContained;
		this.pickup = builder.pickup;
		
		int vehicleSpeed = getVehicleInformation().getVehicleType().getSpeed();
		this.distanceCalculator = new DistanceCalculator(builder.totalDistance, vehicleSpeed);
		//TODO add new Class ArrivalHandler
	}

	
	@Override
	public State proceed() {
		double remainingDistance = distanceCalculator.getRemainingDistance();

		if (remainingDistance > 0) {
			publishPositionUpdates(remainingDistance);
			distanceCalculator.move();

			return this;
		}
		publishPositionUpdates(0);
		if (pickup) {
			return new PickUpState(destination, containersContained, distanceCalculator.getTotalDistance());
		}
		return new OffloadState(containersContained, source, destination, distanceCalculator.getTotalDistance());
	}

	private void publishPositionUpdates(double remainingDistance) {
		UUID selfUUID = getVehicleInformation().getId();

		publisher.publish(new VehiclePositionUpdate(selfUUID, true, remainingDistance, source, destination));

		if(!pickup) {
			for (Container container: containersContained) {
				ContainerInformation containerInformation = Converter.getRemoteContainerInformation(container, selfUUID);
				ContainerPositionUpdate posUpdate = new ContainerPositionUpdate(selfUUID, containerInformation, true);
				publisher.publish(posUpdate);
			}
		}

	}

	
	// Builder pattern:
	public static Builder Builder() {
		return new Builder();
	}
	public static class Builder {
		private UUID source;
		private UUID destination;
		private int totalDistance;
		private Collection<Container> containersContained;
		private boolean pickup;
		
		public Builder() { }

		public Builder withSource(UUID source) {
			this.source = source;
			return this;
		}

		public Builder withDestination(UUID destination) {
			this.destination = destination;
			return this;
		}

		public Builder withTotalDistance(int totalDistance) {
			this.totalDistance = totalDistance;
			return this;
		}

		public Builder withContainersContained(Collection<Container> containersContained) {
			this.containersContained = containersContained;
			return this;
		}

		public Builder setPickup(boolean pickup) {
			this.pickup = pickup;
			return this;
		}
		
		public JourneyState build() {
			return new JourneyState(this);
		}
		
	}


	@Override
	public String toString() {
		return "JourneyState [source=" + source + ", destination=" + destination + ", containersContained="
				+ containersContained + ", pickup=" + pickup + ", remaining distance=" + distanceCalculator.getRemainingDistance() + "]";
	}

}
	
	