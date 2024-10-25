package storage;

import exceptions.FullStorageException;
import map.Location;
import map.MS_Type;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import transport.Container;
import transport.Storage;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class StorageTest {

    private final Storage storage = new Storage(100);

    Location nextHop1 = new Location(UUID.randomUUID(), MS_Type.HUB, "hop1");
    Location nextHop2 = new Location(UUID.randomUUID(), MS_Type.HUB, "hop2");

    Container container1 = new Container(UUID.randomUUID(), 20, UUID.randomUUID(), UUID.randomUUID());
    Container container2 = new Container(UUID.randomUUID(), 10, UUID.randomUUID(), UUID.randomUUID());
    Container container3 = new Container(UUID.randomUUID(), 10, UUID.randomUUID(), UUID.randomUUID());
    Container container4  = new Container(UUID.randomUUID(), 10, UUID.randomUUID(), UUID.randomUUID());
    Container container5  = new Container(UUID.randomUUID(), 60, UUID.randomUUID(), UUID.randomUUID());

    double freeArrivalVehicleSpaceBegin = storage.getFreeArrivalVehicleSpace();

    @BeforeEach
    void setUp() throws InterruptedException {
        storage.addContainerToStorage(nextHop1, container1);
        Thread.sleep(50);
        storage.addContainerToStorage(nextHop2, container2);
        storage.addContainerToStorage(nextHop1, container3);
        storage.addContainerToStorage(nextHop2, container4);
    }

    @Test
    void addingContainerToStorage_shouldUpdateStorageCapacityVariables() {
        double contSum = container1.getWeight() + container2.getWeight() + container3.getWeight() + container4.getWeight();
        assertThat(storage.getFreeArrivalVehicleSpace(), is(equalTo(freeArrivalVehicleSpaceBegin-contSum)));
        assertThat(storage.getOccupiedCapacity(), is(equalTo((int)contSum)));

        assertThat(storage.getStoredContainers().get(nextHop1), hasItems(container1, container3));
        assertThat(storage.getStoredContainers().get(nextHop2), hasItems(container2, container4));

        Executable addingContainer = () -> storage.addContainerToStorage(nextHop1, container5);
        Assertions.assertThrows(FullStorageException.class, addingContainer, "should throw because overflowing hub");
    }

    @Test
    void getPriorityOrder_shouldReturnOrderWithLongestInStorageContainer() {
        Assertions.assertFalse(storage.getPriorityOrder(storage.getStoredContainers()).isEmpty());
        assertThat(storage.getPriorityOrder(storage.getStoredContainers()).get(), is(equalTo(nextHop1)));
    }

    @Test
    void removingContainerFromStorage_shouldUpdateStorageCapacityVariables() {
        double freeArrivalSpaceToBegin = storage.getFreeArrivalVehicleSpace();
        int occupiedCapToBegin = storage.getOccupiedCapacity();

        storage.removeContainer(container1, false);
        Assertions.assertFalse(storage.getStoredContainers().get(nextHop1).contains(container1));
        assertThat(storage.getFreeArrivalVehicleSpace(), is(equalTo(freeArrivalSpaceToBegin + container1.getWeight())));
        assertThat(storage.getOccupiedCapacity(), is(equalTo(occupiedCapToBegin - container1.getWeight())));
    }

}
