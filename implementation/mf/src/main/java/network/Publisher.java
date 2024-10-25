package network;

import messages.AbstractMessage;

import java.util.Queue;

public class Publisher {
	private Queue<AbstractMessage> publishQueue;


	public Publisher(Queue<AbstractMessage> publishQueue) {
		super();
		this.publishQueue = publishQueue;
	}

	public void publish(AbstractMessage message) {
		publishQueue.add(message);
	}

}
