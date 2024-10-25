package main.properties;

import main.database.exceptions.HubOperatorNotFoundException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Properties {

    private static final JsonConvert config = new JsonConvert();

    public static List<UUID> allStations() {
        return config.allEntities().stream()
                .filter(config -> config.getNodeType().equals("station"))
                .map(NetworkEntity::getId)
                .collect(Collectors.toList());
    }

    public static List<UUID> allHubs() {
        return config.allEntities().stream()
                .filter(config -> config.getNodeType().equals("hub"))
                .map(NetworkEntity::getId)
                .collect(Collectors.toList());
    }

    public static NetworkEntity getHubOperator() {
        return config.allEntities().stream()
                .filter(config -> config.getNodeType().equals("hub_operator"))
                .findFirst()
                .orElseThrow(HubOperatorNotFoundException::new);
    }

    public static List<UUID> allNonHubOpEntities() {
        return config.allEntities().stream()
                .filter(config -> !config.getNodeType().equals("hub_operator"))
                .map(NetworkEntity::getId)
                .collect(Collectors.toList());
    }
}
