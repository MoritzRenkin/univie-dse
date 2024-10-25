package network.mM;

import messages.AbstractMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.Port;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

class NetworkOutThread implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(NetworkOutThread.class);
	private Queue<AbstractMessage> publishQueue;
	private NeighbourManager neighbourManager;
	private boolean terminationSignal = false;

	private ExecutorService executor = Executors.newCachedThreadPool(
			new ThreadFactory() {
		@Override
		public Thread newThread(Runnable r) {
			Thread t = Executors.defaultThreadFactory().newThread(r);
			t.setDaemon(true);
			return t;
		}
	});;

	public NetworkOutThread(Queue<AbstractMessage> publishQueue, NeighbourManager neighbourManager) {
		this.publishQueue = publishQueue;
		this.neighbourManager = neighbourManager;
	}

	public void run() {

		if (terminationSignal) {
			throw new IllegalStateException("NetworkOutThread was terminated before start-up.");
		}

		while (!terminationSignal) {
			try {

				if (!publishQueue.isEmpty()) {
					AbstractMessage mes = publishQueue.poll();
					logger.debug("Next Message to send: " + mes);

					for (NetworkNode currNode: neighbourManager.getStaticNeighbours()) {
						executor.execute(new OutgoingMessageHandler(currNode, mes, true));

					}
					List<NetworkNode> dynamicNeighbours = new ArrayList<>(neighbourManager.getDynamicNeighbours().values());

					for (NetworkNode currNode: dynamicNeighbours) {
						executor.execute(new OutgoingMessageHandler(currNode, mes, false));

					}
				}
				Thread.sleep(10);

			} catch (Exception e) {
				logger.error("Unhandled exception ", e);
				return;
			}
		}
	}

	public void terminate() {
		terminationSignal = true;
	}
}
