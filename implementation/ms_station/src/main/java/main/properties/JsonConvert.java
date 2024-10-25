package main.properties;

import exceptions.ConfigException;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class JsonConvert {

    private static final Logger logger = LoggerFactory.getLogger(JsonConvert.class);
    private final Path path;

    public JsonConvert() {
        Path rootPath = Paths.get(System.getProperty("user.dir")).getParent();
        this.path = Paths.get(rootPath.toString(), "config.json");
    }

    public List<NetworkEntity> allEntities() {
        List<NetworkEntity> allConfigs = new ArrayList<>();
        try {
            String contents = new String((Files.readAllBytes(path)));
            JSONObject o = new JSONObject(contents);
            for (var i = o.keys(); i.hasNext(); ) {
                allConfigs.add(readEntity(UUID.fromString(i.next())));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return allConfigs;
    }

    private NetworkEntity readEntity(UUID id) {
        NetworkEntity networkEntity;
        try {
            String contents = new String((Files.readAllBytes(path)));
            JSONObject o = new JSONObject(contents);
            JSONObject value = o.getJSONObject(id.toString());
            var ip = value.getString("ip");
            var port = value.getInt("port");
            var bridgingActivated = value.getBoolean("bridgingActivated");
            var nodeType = value.getString("nodeType");
            networkEntity = new NetworkEntity(id, ip, port, bridgingActivated, nodeType);
        } catch (JSONException | IOException e) {
            logger.error("JsonConvert error JSONException");
            throw new ConfigException(e);
        }

        return networkEntity;
    }
}
