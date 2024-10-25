package main.properties;

import main.localnetwork.exceptions.HubOperatorNotFoundException;
import main.localnetwork.exceptions.StationNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.*;

@Component
public class Properties {

    private static final JsonConvert config = new JsonConvert();

    public static List<UUID> allStations() {
        return getAllEntitiesOfType("station").stream().map(NetworkEntity::getId).collect(toList());
    }

    public static List<UUID> allHubs() {
        return getAllEntitiesOfType("hub").stream().map(NetworkEntity::getId).collect(toList());
    }

    public static UUID getHubOperator() {
        var result =  getAllEntitiesOfType("hub_operator");
        if(!result.isEmpty()) return result.get(0).getId();
        throw new HubOperatorNotFoundException();
    }

    public static NetworkEntity getStation(UUID fromString) {
        return getNetworkEntity(fromString);
    }

    public static String getStationUrl(UUID id){
        var port =  getNetworkEntity(id).getPort();
        var ip = getNetworkEntity(id).getIP();
        return ip + ":" + port;
    }

    private static NetworkEntity getNetworkEntity(UUID id) {
        return config.allEntities().stream()
                .filter(config -> config.getId().equals(id))
                .findFirst()
                .orElseThrow(StationNotFoundException::new);
    }

    private static List<NetworkEntity> getAllEntitiesOfType(String type){
        return config.allEntities().stream()
                .filter(config -> config.getNodeType().equals(type))
                .collect(toList());
    }
}
