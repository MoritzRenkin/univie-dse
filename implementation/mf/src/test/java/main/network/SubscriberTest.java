package main.network;

import exceptions.DuplicateSubcriptionException;
import messages.*;
import network.Callback;
import network.Subscriber;
import org.junit.After;
import org.junit.Test;

import java.util.HashMap;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SubscriberTest {
	private HashMap<Class<? extends AbstractMessage>, Callback<? extends AbstractMessage>> subscribedCallbacks = new HashMap<>();
	private Subscriber subscriber = new Subscriber(subscribedCallbacks);
	private Callback dummyCallback = new Callback<AbstractMessage>() { // no type safety
		@Override
		public void onResponse(AbstractMessage result) { }
	};

	@After
	public void clearSubscriberQueue() {
		subscriber.removeAllSubscribtions();
	}


	@Test(expected = DuplicateSubcriptionException.class)
	public void emptySubscriber_addCallbacksForSameMessagetype_throwsDuplicateSubscriptionException() {
		for (int i=0; i<2; ++i) {
			subscriber.addSubscription(ContainerHandover.class, dummyCallback);
		}
	}

	@Test
	public void emptySubscriber_addCallbacksForDifferentMessagetypes_throwsNothing () {
		subscriber.addSubscription(ContainerHandover.class, dummyCallback);
		subscriber.addSubscription(NewContainerAtSource.class, dummyCallback);
		subscriber.addSubscription(VehicleArrival.class, dummyCallback);
	}


	@Test
	public void emptySubscriber_addSomeSubscribtions_subscriptionsAreAddedToMap() {
		HashMap<Class<? extends AbstractMessage>, Callback<? extends AbstractMessage>> expectedSubscriptions = new HashMap<>();
		expectedSubscriptions.put(ContainerHandover.class, dummyCallback);
		expectedSubscriptions.put(VehicleOrder.class, dummyCallback);
		expectedSubscriptions.put(HubConnectionInformation.class, dummyCallback);
		expectedSubscriptions.put(InstanceOnlineMessage.class, dummyCallback);

		//subscribing dynamically from non parameterised HashMap is not possible, this redundancy is the easiest workaround
		subscriber.addSubscription(ContainerHandover.class, dummyCallback);
		subscriber.addSubscription(VehicleOrder.class, dummyCallback);
		subscriber.addSubscription(HubConnectionInformation.class, dummyCallback);
		subscriber.addSubscription(InstanceOnlineMessage.class, dummyCallback);

		Set<Class<? extends AbstractMessage>> subscribedMessages = this.subscribedCallbacks.keySet();
		Set<Class<? extends AbstractMessage>> expectedSubscribedMessages = expectedSubscriptions.keySet();

		assertTrue(subscribedMessages.containsAll(expectedSubscribedMessages));
		assertTrue(expectedSubscribedMessages.containsAll(subscribedMessages));
	}


	@Test
	public void filledSubscriber_removeSingleSubscribtion_subcriptionsIsNotPresentinMap() {
		subscriber.addSubscription(ContainerHandover.class, dummyCallback);
		subscriber.addSubscription(VehicleOrder.class, dummyCallback);
		subscriber.addSubscription(HubConnectionInformation.class, dummyCallback);
		subscriber.addSubscription(InstanceOnlineMessage.class, dummyCallback);

		Class checkKey = InstanceOnlineMessage.class;
		assertTrue(subscribedCallbacks.containsKey(checkKey));

		subscriber.removeSubscription(checkKey);
		assertFalse(subscribedCallbacks.containsKey(checkKey));

	}

}
