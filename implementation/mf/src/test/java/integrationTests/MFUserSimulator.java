package integrationTests;

import messages.AbstractMessage;
import network.NetworkServiceFactory;
import network.Publisher;
import network.Subscriber;

import java.util.UUID;

public class MFUserSimulator {

    Publisher publisher;
    Subscriber subscriber;
    UUID id;

    public MFUserSimulator(UUID id) {
        this.id = id;

        NetworkServiceFactory.initialize(id);
        publisher = NetworkServiceFactory.getPublisher();
        subscriber = NetworkServiceFactory.getSubscriber();
    }
}
