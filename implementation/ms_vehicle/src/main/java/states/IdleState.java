package states;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import data.Container;
import data.Converter;
import messageUtil.ContainerInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import callbacks.VehicleOrderCallback;
import messages.VehicleOrder;
import network.Callback;

public class IdleState extends State {
	private static Logger logger = LoggerFactory.getLogger(IdleState.class);
	
	private final UUID currentHub;
	private static final Queue<VehicleOrder> orderQueue = new ConcurrentLinkedQueue<>();

	public IdleState(UUID currentHub) {
		super();
		this.currentHub = currentHub;

		if (!subscriber.isSubscriptionPresent(VehicleOrder.class)) {
			addCallback();
		}
	}

	@Override
	public State proceed() {

		if (orderQueue.isEmpty()) {
			return this;
			
		} else {
			
			logger.debug("Order Queue not empty: " + orderQueue + ". Asserting size==1");
			assert(orderQueue.size() == 1);
			VehicleOrder order = orderQueue.poll();
			logger.debug("New VehicleOrder content: " + order);
			assert(currentHub.equals(order.getSenderUUID()));

			//subscriber.removeSubscription(VehicleOrder.class); Can get order even when in other states

			List<Container> convertedContainerList = new ArrayList<>();
			for (ContainerInformation remote: order.getContainersToCarry()) {
				convertedContainerList.add(Converter.getLocalContainer(remote));
			}

			return JourneyState.Builder()
					.withSource(currentHub)
					.withDestination(order.getTarget())
					.withTotalDistance(order.getDistance())
					.withContainersContained(convertedContainerList)
					.setPickup(order.isPickup())
					.build();
		}
	}

	private void addCallback() {

		Callback<VehicleOrder> callback = new VehicleOrderCallback(getVehicleInformation().getId(), orderQueue);
		subscriber.addSubscription(VehicleOrder.class, callback);
	}

	@Override
	public String toString() {
		return "IdleState, currentHub: " + currentHub + ", orders: " + orderQueue;
	}

}
