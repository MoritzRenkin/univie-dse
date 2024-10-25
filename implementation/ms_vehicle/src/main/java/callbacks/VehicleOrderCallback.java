package callbacks;

import java.util.Queue;
import java.util.UUID;

import messages.VehicleOrder;
import network.Callback;

public class VehicleOrderCallback implements Callback<VehicleOrder>{
	
	private UUID selfUUID;
	private Queue<VehicleOrder> orderQueue;
	

	public VehicleOrderCallback(UUID selfUUID, Queue<VehicleOrder> orderQueue) {
		super();
		this.selfUUID = selfUUID;
		this.orderQueue = orderQueue;
	}

	@Override
	public void onResponse(VehicleOrder message) {
		if (selfUUID.equals(message.getVehicle())) {
			orderQueue.add(message);
		}
				
	}

	public Queue<VehicleOrder> getOrderQueue() {
		return orderQueue;
	}
	
	

}
