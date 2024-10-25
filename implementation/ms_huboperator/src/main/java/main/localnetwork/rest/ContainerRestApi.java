package main.localnetwork.rest;

import main.database.ContainerHistoryState;
import main.database.ContainerState;
import main.database.DatabaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RestController
public class ContainerRestApi {

    @Autowired
    private DatabaseController dbController;
    private final String SERVER = "http://localhost:4200";

    public ContainerRestApi() {
    }

    @CrossOrigin(origins = SERVER)
    @GetMapping("/container/location/")
    List<ContainerReply> allContainer() {
        return dbController.getAllContainers().stream().map(ContainerReply::new).collect(toList());
    }

    @CrossOrigin(origins = SERVER)
    @GetMapping("/container/location/{uuid}")
    ContainerReply getContainerById(@PathVariable("uuid") String uuid) {
        return new ContainerReply(dbController.getContainerState(UUID.fromString(uuid)));
    }

    @CrossOrigin(origins = SERVER)
    @GetMapping("/container/history/")
    List<SingleContainerHistoryReply> allContainerHistory() {
        return dbController.getAllContainerHistories().stream().map(SingleContainerHistoryReply::new).collect(toList());
    }

    @CrossOrigin(origins = SERVER)
    @GetMapping("/container/history/{uuid}")
    ContainerHistoryReply getContainerHistoryById(@PathVariable("uuid") String uuid) {
        return new ContainerHistoryReply(dbController.getContainerHistoryState(UUID.fromString(uuid)));
    }
}

class ContainerReply {
    private final UUID containerId;
    private final UUID currentLocation;
    private final double distanceToGo;


    public ContainerReply(ContainerState state) {
        this.containerId = state.getContainerId();
        this.currentLocation = state.getCurrentLocation();
        this.distanceToGo = state.getDistanceToGo();
    }

    public UUID getContainerId() {
        return containerId;
    }

    public UUID getCurrentLocation() {
        return currentLocation;
    }

    public double getDistanceToGo() {
        return distanceToGo;
    }
}

class ContainerHistoryReply {
    private final UUID containerId;
    private final List<UUID> visitedLocation;

    public ContainerHistoryReply(List<ContainerHistoryState> state) {
        if(!state.isEmpty()) {
            this.containerId = state.get(0).getContainerId();
            this.visitedLocation = state.stream().map(ContainerHistoryState::getLocationId).collect(Collectors.toList());
        } else {
            this.containerId = null;
            this.visitedLocation = new ArrayList<>();
        }
    }

    public UUID getContainerId() {
        return containerId;
    }

    public List<UUID> getVisitedLocation() {
        return visitedLocation;
    }
}

class SingleContainerHistoryReply {
    private final UUID containerId;
    private final UUID locationId;

    public SingleContainerHistoryReply(ContainerHistoryState state) {
        this.containerId = state.getContainerId();
        this.locationId = state.getLocationId();
    }

    public UUID getContainerId() {
        return containerId;
    }

    public UUID getLocationId() {
        return locationId;
    }
}
