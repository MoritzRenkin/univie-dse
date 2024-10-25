package network.mM;

import messages.AbstractMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.util.NoSuchElementException;

public class OutgoingMessageHandler implements Runnable {

    private NetworkNode networkNode;
    private AbstractMessage message;
    private String neighbourType;

    private static Logger logger = LoggerFactory.getLogger(OutgoingMessageHandler.class);

    public OutgoingMessageHandler(NetworkNode networkNode, AbstractMessage message, boolean staticNeighbour) {
        this.networkNode = networkNode;
        this.message = message;
        if (staticNeighbour) {
            neighbourType = "static";
        } else {
            neighbourType = "dynamic";
        }
    }

    @Override
    public void run() {
        Socket socket;
        try {
            logger.debug("Sending Message to " + neighbourType + " neighbour. IP: " + networkNode.getIp() + " PORT: " + networkNode.getPort());
            socket = new Socket(networkNode.getIp(),
                    networkNode.getPort());

            OutputStream outputStream = socket.getOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

            objectOutputStream.writeObject(message);

            objectOutputStream.close();
            outputStream.close();
            socket.close();
        } catch (ConnectException e) {
            logger.warn(
                    "Failed to send a message to " + neighbourType + " neighbour: IP " + networkNode.getIp()
                            + " PORT " + networkNode.getPort() + "; Message: " + message.toString());


        } catch (IOException e) {
            logger.error("IOException, Message: " + message.toString(), e);
            return;
        } catch (NoSuchElementException e) {
            logger.warn("No such element exception", e);
            return;
        }
    }
}
