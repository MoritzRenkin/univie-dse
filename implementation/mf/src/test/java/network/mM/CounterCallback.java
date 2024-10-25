package network.mM;

import network.Callback;
import messages.VehicleOrder;

import java.util.concurrent.atomic.AtomicInteger;

public class CounterCallback implements Callback<VehicleOrder> {
	private AtomicInteger callbackInvocationCount;

	public CounterCallback(AtomicInteger callbackInvocationCount) {
		super();
		this.callbackInvocationCount = callbackInvocationCount;
	}

	@Override
	public void onResponse(VehicleOrder result) {
		callbackInvocationCount.incrementAndGet();
	}

}
