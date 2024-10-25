package main.localnetwork.rest;

import main.database.DatabaseController;
import main.database.HubState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class HubRestApi {

    @Autowired
    private DatabaseController dbController;
    private final String SERVER = "http://localhost:4200";

    @CrossOrigin(origins = SERVER)
    @GetMapping("/hub/occupation/")
    List<HubReply> allHubs() {
        return dbController.getAllHubs().stream().map(HubReply::new).collect(Collectors.toList());
    }

    @CrossOrigin(origins = SERVER)
    @GetMapping("/hub/occupation/{uuid}")
    HubReply getHubById(@PathVariable("uuid") String uuid) {
        return new HubReply(dbController.getHubState(UUID.fromString(uuid)));
    }

    @Autowired
    public void setDbController(DatabaseController dbController) {
        this.dbController = dbController;
    }
}

class HubReply {
    private final UUID hubId;
    private final double fillingLevel;

    public HubReply(HubState state) {
        this.hubId = state.getHubId();
        this.fillingLevel = state.getFillingLevel();
    }

    public UUID getHubId() {
        return hubId;
    }

    public double getFillingLevel() {
        return fillingLevel;
    }
}
