package network.mM;

import messages.AbstractMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Queue;

public class IncomingMessageHandler implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(IncomingMessageHandler.class);
	private Socket client;
	private Queue<AbstractMessage> incomingMessagesQueue;

	public IncomingMessageHandler(Socket client, Queue<AbstractMessage> incomingMessagesQueue) {
		this.client = client;
		this.incomingMessagesQueue = incomingMessagesQueue;
	}

	@Override
	public void run() {
		try {
			ObjectInputStream objectInput = new ObjectInputStream(client.getInputStream());
			try {
				AbstractMessage mes = (AbstractMessage) objectInput.readObject();
				incomingMessagesQueue.add(mes);
				//logger.debug("Message from " + mes.getSenderUUID() + " to " + client + " MesUUID: " + mes.getMessageUUID());
				
				objectInput.close();
			} catch (ClassNotFoundException e) {
				logger.warn("ClassNotFoundException " + client);
				e.printStackTrace();
			}
		} catch (IOException e) {
			logger.warn("IOException " + client);
			e.printStackTrace();
		}

	}
}
