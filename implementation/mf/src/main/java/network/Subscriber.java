package network;

import exceptions.DuplicateSubcriptionException;
import messages.AbstractMessage;

import java.util.Map;
import java.util.Optional;

public class Subscriber {
	private Map<Class<? extends AbstractMessage>, Callback<? extends AbstractMessage>> messageCallbacks;

	public Subscriber(Map<Class<? extends AbstractMessage>, Callback<? extends AbstractMessage>> messageCallbacks) {
		super();
		this.messageCallbacks = messageCallbacks;
	}

	/**
	 * Subscribes to all incoming messages of type messageClass.
	 * If a message of the desired type arrives for the first time, the callback is executed.
	 * Only one callback/subscription is possible per messageClass.
	 */
	public <T extends AbstractMessage> void addSubscription(Class<T> messageClass, Callback<T> callback) {
		if (messageCallbacks.containsKey(messageClass)) {
			throw new DuplicateSubcriptionException("Only one Callback per MessageClass. Concerned MessageClass: " + messageClass);
		}
    	messageCallbacks.put(messageClass, callback);
    }

	/**
	 * Check if a MessageClass already has an active subscription.
	 * @return True if there is a subscription/callback active. False otherwise.
	 */
	public boolean isSubscriptionPresent(Class<? extends AbstractMessage> messageClass) {
		return messageCallbacks.containsKey(messageClass);
	}

	/**
	 * Removes the callback/subscription for the messageClass
	 * @param messageClass
	 * @return True if a callback/subscription was removed. False otherwise.
	 */
    public boolean removeSubscription(Class<? extends AbstractMessage> messageClass) {
    	Object removedElem = messageCallbacks.remove(messageClass);
    	return removedElem != null;
    }

	/**
	 * Returns an Optional of the callback if present or of null if not present.
	 * NOTE: Do not callbacks on your own except for testing purposes.
	 * @param messageClass
	 * @return Optional of unparameterized callback or null.
	 */
	public Optional<Callback> getCallback(Class<? extends AbstractMessage> messageClass) {
    	return  Optional.ofNullable(messageCallbacks.get(messageClass));
	}

    public void removeAllSubscribtions() {
    	messageCallbacks.clear();
    }
}
