package main;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.VehicleInformation;
import messageUtil.EMicroservice;
import messages.InstanceOnlineMessage;
import network.Callback;
import network.GenericDataCalback;
import states.IdleState;
import states.State;

import network.NetworkServiceFactory;
import network.Publisher;
import network.Subscriber;


public class Controller {
	private static Logger logger = LoggerFactory.getLogger(Controller.class);
	private static final int STATE_REFRESH_TIME = 3000; //in ms

	private final VehicleInformation vehicleInformation;
	private State currState;
	
	public Controller(UUID vehicleId) {
		super();
		this.vehicleInformation = new ConfigReader().getVehicleInformationFromConfig(vehicleId);
	}
	
	
	public void startMicroService() throws InterruptedException {
		NetworkServiceFactory.initialize(vehicleInformation.getId());
		State.setVehicleInformation(vehicleInformation);

		awaitStartUp();

		currState = new IdleState(vehicleInformation.getMotherHubId());
		
		logger.info("Vehicle Microservice " + vehicleInformation.getId() + " started operation with state:" + currState.toString());
		
		while(true) {
			State previousState = currState;
			currState = currState.proceed();
			
			if (previousState != currState) {
				logger.info("NEW State: " + currState);
			} else {
				//logger.debug("Current State: " + currState);
			}

			Thread.sleep(STATE_REFRESH_TIME);
			
		}	
	}
	
	
	private void awaitStartUp() throws InterruptedException {
		Publisher publisher = NetworkServiceFactory.getPublisher();
		Subscriber subscriber = NetworkServiceFactory.getSubscriber();
		
		UUID selfUUID = vehicleInformation.getId();
		publisher.publish(new InstanceOnlineMessage(selfUUID, EMicroservice.VEHICLE));
		logger.info("Sent InstanceOnlineMessage");
		
		AtomicBoolean startSignal = new AtomicBoolean(false);
		Callback<InstanceOnlineMessage> callback = new GenericDataCalback<InstanceOnlineMessage, AtomicBoolean>(startSignal) {

			@Override
			public void onResponse(InstanceOnlineMessage message) {
				if (message.getType() == EMicroservice.HUB_OPERATOR) {
					this.data.set(true);
				}
			}
		};
		subscriber.addSubscription(InstanceOnlineMessage.class, callback);
		
		logger.info("Waiting for Start Signal");
		while(!startSignal.get()) {
			Thread.sleep(50);
		}
		logger.info("Start Signal received");
		subscriber.removeSubscription(InstanceOnlineMessage.class);
	}


	public State getCurrState() {
		return currState;
	}

	public static int getSTATE_REFRESH_TIME() {
		return Controller.STATE_REFRESH_TIME;
	}
}
