package main;

import map.HubNeighbourInformation;
import map.Location;
import map.MS_Type;
import network.NetworkServiceFactory;
import org.junit.jupiter.api.*;
import transport.Container;
import transport.EVehicleTransportState;
import transport.Vehicle;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MainControllerTest {
	private static final MainController mainController = new MainController(UUID.fromString("61297ae3-b7bf-474a-b839-7256d63b06c6"));

	private final UUID station1 = UUID.fromString("1f805130-4db8-4523-b4af-8a3a0bf9f6c9");
	private final UUID station2 = UUID.fromString("f7dec03a-b120-46dc-b919-db7d3432fe92");
	private final UUID station3 = UUID.fromString("627187b2-89ea-44f8-b45c-0ffc3b319736");

	private final Container container1 = new Container(UUID.randomUUID(), 20, station1, station2);
	private final Container container2 = new Container(UUID.randomUUID(), 10, station1, station2);
	private final Container container3 = new Container(UUID.randomUUID(), 10, station2, station3);
	private final Container container4 = new Container(UUID.randomUUID(), 10, station2, station3);
	private final Container container5 = new Container(UUID.randomUUID(), 60, station2, station3);
	private final Container container6 = new Container(UUID.randomUUID(), 60, station2, station3);

	@BeforeAll
	public static void setUp() {
		mainController.initializeHub(UUID.fromString("61297ae3-b7bf-474a-b839-7256d63b06c6"));
		Queue<HubNeighbourInformation> connections = new ConcurrentLinkedQueue<>();
		mainController.getMapManager().addHubNeighbourConnectionsToMap(connections); //initalizing route Calculator
	}

	@AfterAll
	public static void tearDown() {
		mainController.getNetworkController().getSubscriber().removeAllSubscribtions();
		NetworkServiceFactory.terminate();
	}


	@Test
	public void addAllWaitingIdleVehicles_shouldPutVehiclesInQueueBackToStorage() {
		Set<Vehicle> vehicles = mainController.getVehicleManager().getStoredVehicles();
		for (Vehicle vehicle : vehicles) {
			if (vehicle.getVehicleID().equals(UUID.fromString("38d10f47-8d89-4740-93c8-07408744ad87"))) {
				vehicle.setVehicleTransportState(EVehicleTransportState.TRANSPORT);
			}
			if (vehicle.getVehicleID().equals(UUID.fromString("e9486be9-bbf8-4a72-a026-25b852adddc9"))) {
				vehicle.setVehicleTransportState(EVehicleTransportState.TRANSPORT);
			}
		}

		mainController.getNetworkController().getIdleVehicleArrivalQueue().add(UUID.fromString("38d10f47-8d89-4740-93c8-07408744ad87"));
		mainController.getNetworkController().getIdleVehicleArrivalQueue().add(UUID.fromString("e9486be9-bbf8-4a72-a026-25b852adddc9"));

		mainController.addAllWaitingIdleVehicles();

		assertThat(mainController.getVehicleManager().getAvailableVehicles(), hasItems(new Vehicle(UUID.fromString("38d10f47-8d89-4740-93c8-07408744ad87")),
				new Vehicle(UUID.fromString("e9486be9-bbf8-4a72-a026-25b852adddc9"))));
		assertThat(mainController.getNetworkController().getIdleVehicleArrivalQueue().isEmpty(), is(true));
	}


	@Test
	@Order(1)
	public void processingOfPickUpOrders_shouldPutOrdersInPickUpMap() throws InterruptedException {
		mainController.getNetworkController().getNewContainerAtSourceQueue().add(container3);
		mainController.getNetworkController().getNewContainerAtSourceQueue().add(container4);
		mainController.getNetworkController().getNewContainerAtSourceQueue().add(container5);
		mainController.getNetworkController().getNewContainerAtSourceQueue().add(container6);
		Thread.sleep(1000);
		mainController.getNetworkController().getNewContainerAtSourceQueue().add(container1);
		mainController.getNetworkController().getNewContainerAtSourceQueue().add(container2);


		mainController.processPickUpOrders();

		assertThat(mainController.getStorage().getPickupOrders().get(new Location(station1, MS_Type.STATION)), hasItems(container1, container2));
		assertThat(mainController.getStorage().getPickupOrders().get(new Location(station2, MS_Type.STATION)), hasItems(container3, container4, container5, container6));

	}


	@Test
	@Order(2)
	void sendOutContainer_shouldSendVehicleToPickUpContainer() throws InterruptedException {
		mainController.sendOutContainers(mainController.getStorage().getPickupOrders(), true);

		Assertions.assertTrue(mainController.getStorage().getPickupOrders().get(new Location(station2, MS_Type.STATION)).isEmpty());
		Assertions.assertTrue(mainController.getStorage().getPickupOrders().get(new Location(station1, MS_Type.STATION)).isEmpty());
		assertThat(mainController.getVehicleManager().getStoredVehicles().size(), is(not(equalTo(mainController.getVehicleManager().getAvailableVehicles().size()))));

		for (Vehicle vehicle : mainController.getVehicleManager().getStoredVehicles()) {
			mainController.getVehicleManager().addVehicleBackToStorage(vehicle.getVehicleID());
		}
		assertThat(mainController.getVehicleManager().getStoredVehicles().size(), is(equalTo(mainController.getVehicleManager().getAvailableVehicles().size())));
	}



	@Test
	@Order(3)
	void processingForeignVehicleArrivals_shouldPutContainersToStorage() {
		Set<Container> containersInVehicle1 = new HashSet<>();
		containersInVehicle1.add(container1);
		containersInVehicle1.add(container2);
		containersInVehicle1.add(container3);
		Vehicle foreignVehicle1 = new Vehicle(UUID.randomUUID(), containersInVehicle1);

		Set<Container> containersInVehicle2 = new HashSet<>();
		containersInVehicle1.add(container4);
		containersInVehicle1.add(container5);
		containersInVehicle1.add(container6);
		Vehicle foreignVehicle2 = new Vehicle(UUID.randomUUID(), containersInVehicle2);

		mainController.getNetworkController().getForeignFullVehicleArrivalQueue().add(foreignVehicle1);
		mainController.getNetworkController().getForeignFullVehicleArrivalQueue().add(foreignVehicle2);

		mainController.processForeignVehicleArrivals();

		assertThat(mainController.getStorage().getStoredContainers().get(new Location(station2, MS_Type.STATION)), hasItems(container1, container2));
		assertThat(mainController.getStorage().getStoredContainers().get(new Location(station3, MS_Type.STATION)), hasItems(container3, container4, container5, container6));

	}


	@Test
	@Order(4)
	void sendOutContainer_shouldSendVehiclesToTransportContainers() throws InterruptedException {
		mainController.sendOutContainers(mainController.getStorage().getStoredContainers(), false);

		Assertions.assertTrue(mainController.getStorage().getStoredContainers().get(new Location(station2, MS_Type.STATION)).isEmpty());
		Assertions.assertTrue(mainController.getStorage().getStoredContainers().get(new Location(station3, MS_Type.STATION)).isEmpty());

	}

	@Test
	void tooMuchOrdersToTheSameStation_shouldSendEverythingOut() throws InterruptedException {
		mainController.getVehicleManager().getStoredVehicles().stream()
				.filter(v -> v.getVehicleCapacity() == 30)
				.forEach(v -> v.setVehicleTransportState(EVehicleTransportState.TRANSPORT));

		Container container1 = new Container(UUID.randomUUID(), 100, station1, station2);
		Container container2 = new Container(UUID.randomUUID(), 100, station1, station2);
		Container container3 = new Container(UUID.randomUUID(), 50, station1, station2);
		Container container4 = new Container(UUID.randomUUID(), 10, station1, station2);
		Container container5 = new Container(UUID.randomUUID(), 10, station1, station2);
		Container container6 = new Container(UUID.randomUUID(), 60, station1, station2);

		Location locStation2 = new Location(station2, MS_Type.STATION);

		mainController.getStorage().addContainerToStorage(locStation2, container1);
		mainController.getStorage().addContainerToStorage(locStation2, container2);
		mainController.getStorage().addContainerToStorage(locStation2, container3);
		mainController.getStorage().addContainerToStorage(locStation2, container4);
		mainController.getStorage().addContainerToStorage(locStation2, container5);
		mainController.getStorage().addContainerToStorage(locStation2, container6);


		mainController.sendOutContainers(mainController.getStorage().getStoredContainers(), false);

		assertThat(mainController.getVehicleManager().getAvailableVehicles(), is(empty()));
		assertThat(mainController.getStorage().getStoredContainers().get(locStation2), is(empty()));

		for (Vehicle vehicle : mainController.getVehicleManager().getStoredVehicles()) {
			mainController.getVehicleManager().addVehicleBackToStorage(vehicle.getVehicleID());
		}
	}


}
