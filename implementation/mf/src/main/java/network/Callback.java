package network;

import messages.AbstractMessage;

public interface Callback<T extends AbstractMessage> {

    /**
     * Must be thread-safe. Never catch InterruptedExceptions without re-throwing the exception!
     * This might prevent the thread from exiting.
     */
    void onResponse(T message);
}
