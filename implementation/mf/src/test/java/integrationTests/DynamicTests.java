package integrationTests;

import messageUtil.EMicroservice;
import messages.AbstractMessage;
import messages.InstanceOnlineMessage;
import network.Callback;
import network.GenericDataCalback;
import network.NetworkServiceFactory;
import org.junit.*;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.UUID;
import java.util.Vector;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class DynamicTests {

    private static final int DEFAULT_WAIT_TIME = 1000;

    private static final UUID selfUUID = UUID.fromString("769ccd0f-c749-4ad9-a275-7dd1b44960c1");
    private static final PublishChecker publishChecker = new PublishChecker(9004);
    private static final MFUserSimulator simulator = new MFUserSimulator(selfUUID);




    @AfterClass
    public static void terminateThreads() {
        NetworkServiceFactory.terminate();
        publishChecker.terminate();
    }

    @After
    public void clearData() {
        publishChecker.getMessagesReceived().clear();
        simulator.subscriber.removeAllSubscribtions();
    }

    /**
     * This requires changes in the config: Hub id 4 must have ip localhost.
     * @throws InterruptedException
     */
    //@Test
    public void messageSentOverMessagingFramework_messageReceived() throws InterruptedException {
        Thread t = new Thread(publishChecker);
        t.start();

        AbstractMessage msg = new InstanceOnlineMessage(selfUUID, EMicroservice.STATION);

        simulator.publisher.publish(msg);
        System.out.println("Message published");

        Thread.sleep(DEFAULT_WAIT_TIME);

        List<AbstractMessage> messagesReceived = publishChecker.getMessagesReceived();
        assertEquals(1, messagesReceived.size());
        assertTrue(messagesReceived.get(0) instanceof InstanceOnlineMessage);

        publishChecker.terminate();
        Thread.sleep(DEFAULT_WAIT_TIME);
        assert(!t.isAlive());
    }

    //@Test
    public void simlatorSubscribedMessage_messageSentToSimulator_callbackCalled() throws IOException, InterruptedException {
        Vector<AbstractMessage> messagesReceivedInCallback = new Vector<>();
        Callback<InstanceOnlineMessage> callback = new GenericDataCalback<InstanceOnlineMessage, Vector<AbstractMessage>>(messagesReceivedInCallback) {
            @Override
            public void onResponse(InstanceOnlineMessage message) {
                this.data.add(message);
            }
        };

        simulator.subscriber.addSubscription(InstanceOnlineMessage.class, callback);

        Socket socket = new Socket(InetAddress.getLocalHost(), 9010);
        OutputStream sockOut = socket.getOutputStream();
        ObjectOutputStream objOut = new ObjectOutputStream(sockOut);

        AbstractMessage msg = new InstanceOnlineMessage(UUID.randomUUID(), EMicroservice.STATION);
        objOut.writeObject(msg);

        objOut.close();
        sockOut.close();
        socket.close();

        Thread.sleep(DEFAULT_WAIT_TIME);

        assertEquals(1, messagesReceivedInCallback.size());

    }


}
