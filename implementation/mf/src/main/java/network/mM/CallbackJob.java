package network.mM;

import network.Callback;
import messages.AbstractMessage;

class CallbackJob implements Runnable {

	private Callback callback;
	private AbstractMessage message;

	public CallbackJob(Callback callback, AbstractMessage message) {
		super();
		this.callback = callback;
		this.message = message;
	}

	@Override
	public void run() {
		callback.onResponse(message);
	}

}
