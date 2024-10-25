package integrationTests;

import messages.AbstractMessage;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

public class PublishChecker implements Runnable{

    ServerSocket serverSocket;

    boolean terminateSignal = false;

    List<AbstractMessage> messagesReceived = new Vector<>();

    public PublishChecker(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        while(!terminateSignal) {
            try {
                Socket clientSocket = serverSocket.accept();
                InputStream sockIn = clientSocket.getInputStream();
                ObjectInputStream objIn = new ObjectInputStream(sockIn);

                AbstractMessage msg = (AbstractMessage) objIn.readObject();
                messagesReceived.add(msg);

                objIn.close();
                sockIn.close();
                clientSocket.close();

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
    }


    public List<AbstractMessage> getMessagesReceived() {
        return messagesReceived;
    }

    public void terminate() {
        terminateSignal = true;
    }
}
