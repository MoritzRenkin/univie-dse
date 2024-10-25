package main;

import java.util.UUID;

public class Main {

	public static void main(String[] args) {
		final UUID myUUID = UUID.fromString(args[0]);
		final int weight = Integer.parseInt(args[1]);
		final int port = Integer.parseInt(args[2]);
		final UUID current = UUID.fromString(args[3]);
		final UUID destination = UUID.fromString(args[4]);

		Controller controller = new Controller(myUUID, weight, port, current, destination);
		controller.startMicroService();
	}
}
