package main;

import network.NetworkServiceFactory;

import java.util.UUID;

public class Main {

    public static void main(String[] args) {
        final UUID myUUID = UUID.fromString(args[0]);

        MainController controller = new MainController(myUUID);

        try {
            controller.startHubService();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                NetworkServiceFactory.terminate();
            } catch (Exception e) {
                System.exit(1);
            }
        }

    }
}
