package main;

import jsonConvert.JsonConverter;
import jsonConvert.StartUpInfo;
import map.HubNeighbourInformation;
import map.Location;
import map.MS_Type;
import network.NetworkServiceFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
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

//IntegrationTest
class ContainerRoutingTest {

    private final static UUID myHub = UUID.fromString("61297ae3-b7bf-474a-b839-7256d63b06c6"); //hub1
    private final static UUID hub2 = UUID.fromString("fbe0539e-c16c-4472-b78b-30c9009726d3");
    private final static UUID hub3 = UUID.fromString("5f1822ca-8408-44b0-9ece-c397c73b1be2");
    private final static UUID hub4 = UUID.fromString("3346ced8-c994-4df2-b43d-c1497c20fa86");

    private static final Queue<HubNeighbourInformation> hubNeighbourInformations = new ConcurrentLinkedQueue<>();

    private static final MainController mainController = new MainController(myHub);

    @BeforeAll
    public static void setUp() {
        StartUpInfo hub2_info = new JsonConverter(hub2).convert();
        StartUpInfo hub3_info = new JsonConverter(hub3).convert();
        StartUpInfo hub4_info = new JsonConverter(hub4).convert();

        hubNeighbourInformations.add(new HubNeighbourInformation(hub2_info.getNeighbourConnections()));
        hubNeighbourInformations.add(new HubNeighbourInformation(hub3_info.getNeighbourConnections()));
        hubNeighbourInformations.add(new HubNeighbourInformation(hub4_info.getNeighbourConnections()));

    }

    @AfterAll
    public static void tearDown() {
        mainController.getNetworkController().getSubscriber().removeAllSubscribtions();
        NetworkServiceFactory.terminate();
    }

    @Test
    public void routingContainers() throws InterruptedException {
        mainController.initializeHub(myHub);
        mainController.getMapManager().addHubNeighbourConnectionsToMap(hubNeighbourInformations);

        Location source1 = new Location(UUID.fromString("1f805130-4db8-4523-b4af-8a3a0bf9f6c9"), MS_Type.STATION);
        Location destination = new Location(UUID.fromString("769ccd0f-c749-4ad9-a275-7dd1b44960c1"), MS_Type.STATION);
        Container container1 = new Container(UUID.randomUUID(), 14, source1.getID(), destination.getID());
        mainController.getNetworkController().getNewContainerAtSourceQueue().add(container1);

        //use only one vehicle
        for (Vehicle v : mainController.getVehicleManager().getStoredVehicles()) {
            if (!v.getVehicleID().equals(UUID.fromString("38d10f47-8d89-4740-93c8-07408744ad87"))) {
                v.setVehicleTransportState(EVehicleTransportState.TRANSPORT);
            }
        }

        Set<Container> containersContained = new HashSet<>();
        containersContained.add(container1);

        mainController.processPickUpOrders();
        assertThat(mainController.getStorage().getPickupOrders().get(source1), hasItems(container1));
        assertThat(mainController.getStorage().getOccupiedCapacity(), is(0));
        assertThat(mainController.getStorage().getFreePickUpOrderCapacity(), is(equalTo(mainController.getStorage().getHubCapacity() * 0.4 -
                container1.getWeight())));

        Thread.sleep(5050);
        mainController.sendOutContainers(mainController.getStorage().getPickupOrders(), true);
        assertThat(mainController.getStorage().getPickupOrders().get(source1), is(empty()));
        assertThat(mainController.getStorage().getOccupiedCapacity(), is(equalTo(0)));
        assertThat(mainController.getVehicleManager().getAvailableVehicles(), is(empty()));

        mainController.getNetworkController().getMyFullVehicleArrivalQueue().add(new Vehicle(UUID.fromString("38d10f47-8d89-4740-93c8-07408744ad87"),
                containersContained));
        mainController.processOwnVehicleArrivals();
        assertThat(mainController.getStorage().getOccupiedCapacity(), is(equalTo(container1.getWeight())));
        assertThat(mainController.getVehicleManager().getAvailableVehicles(), hasItem(new Vehicle(UUID.fromString("38d10f47-8d89-4740-93c8-07408744ad87"))));
        Assertions.assertFalse(mainController.getStorage().getStoredContainers().isEmpty());

        Thread.sleep(5050);
        mainController.sendOutContainers(mainController.getStorage().getStoredContainers(), false);
        for (Location location : mainController.getStorage().getStoredContainers().keySet()) {
            assertThat(mainController.getStorage().getStoredContainers().get(location), is(empty()));
        }
        assertThat(mainController.getVehicleManager().getAvailableVehicles(), is(empty()));
        assertThat(mainController.getStorage().getOccupiedCapacity(), is(equalTo(0)));

        mainController.getVehicleManager().addVehicleBackToStorage(UUID.fromString("38d10f47-8d89-4740-93c8-07408744ad87"));

        containersContained.clear();
        Location source8 = new Location(UUID.fromString("54b3a4a0-ef89-44f0-ac0c-f1f317bc3598"), MS_Type.STATION);
        Container container2 = new Container(UUID.randomUUID(), 10, source8.getID(), source1.getID()); //next hop source1
        containersContained.add(container2);

        mainController.getNetworkController().getForeignFullVehicleArrivalQueue().add(new Vehicle(UUID.fromString("38d10f47-8d89-4740-93c8-07408744ad87"),
                containersContained));
        mainController.processForeignVehicleArrivals();
        assertThat(mainController.getStorage().getStoredContainers().get(source1), hasItem(container2));
        assertThat(mainController.getStorage().getOccupiedCapacity(), is(equalTo(container2.getWeight())));

        Thread.sleep(5050);
        mainController.sendOutContainers(mainController.getStorage().getStoredContainers(), false);
        assertThat(mainController.getStorage().getStoredContainers().get(new Location(hub3, MS_Type.HUB)), is(empty()));
        assertThat(mainController.getVehicleManager().getAvailableVehicles(), is(empty()));
        assertThat(mainController.getStorage().getOccupiedCapacity(), is(equalTo(0)));

    }
}
