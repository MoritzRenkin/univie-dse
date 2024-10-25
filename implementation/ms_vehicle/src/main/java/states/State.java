package states;

import data.VehicleInformation;
import exceptions.AbortException;
import network.NetworkServiceFactory;
import network.Publisher;
import network.Subscriber;

public abstract class State {
	
	private static VehicleInformation vehicleInformation;
	protected static Publisher publisher = NetworkServiceFactory.getPublisher();
	protected static Subscriber subscriber = NetworkServiceFactory.getSubscriber();

	
	public State() {
		super();
		if (vehicleInformation == null) {
			throw new IllegalStateException("vehicleInformation must be set before any state can be initialized.");
		}
	}
	
		public abstract State proceed();
	
	public State abort() throws AbortException {
		throw new AbortException("State aborted: " + this.toString());
	};

	public static VehicleInformation getVehicleInformation() {
		return vehicleInformation;
	}

	public static void setVehicleInformation (VehicleInformation vehicleInformation) {
		State.vehicleInformation = vehicleInformation;
	}
	
	@Override
	public abstract String toString();

}
