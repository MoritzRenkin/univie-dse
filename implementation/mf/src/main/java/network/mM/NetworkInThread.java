package network.mM;

import messages.AbstractMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

class NetworkInThread implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(NetworkInThread.class);

	private Queue<AbstractMessage> incomingMessagesQueue;
	private int port;
	private ServerSocket server;
	private ExecutorService exec;

	public NetworkInThread(Queue<AbstractMessage> incomingMessagesQueue, int port) {
		this.incomingMessagesQueue = incomingMessagesQueue;
		this.port = port;
	}

	public void run() {
		try {

			server = new ServerSocket(port);

			 exec = Executors.newCachedThreadPool(
					new ThreadFactory() {
						@Override
						public Thread newThread(Runnable r) {
							Thread t = Executors.defaultThreadFactory().newThread(r);
							// Alle Handler werden so zu daemon threads, werden also automatisch beendet.
			                t.setDaemon(true);
			                return t;
						}
					});

			logger.info("Started listening for incoming connections at port " + port);

			while (true) {
				Socket client = server.accept();
				exec.execute(new IncomingMessageHandler(client, incomingMessagesQueue));
				//handleIncomingMessage(client);
			}

		} catch (Exception e) {
			logger.error("Unspecified exception. Halting", e);
			return;

		}finally {
			try {
				logger.debug("Closing Server Socket");
				server.close();
				//exec.shutdownNow();
			} catch (IOException e) {
				logger.error("Error closing ServerSocket", e);
			}
		}
	}


	private void handleIncomingMessage(Socket client) {
		try {
			ObjectInputStream objectInput = new ObjectInputStream(client.getInputStream());
			try {
				AbstractMessage mes = (AbstractMessage) objectInput.readObject();
				incomingMessagesQueue.add(mes);
				logger.debug("Message from " + mes.getSenderUUID() + " to " + client + " MesUUID: " + mes.getMessageUUID());

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
