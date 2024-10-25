package main;

import data.VehicleInformation;
import data.VehicleType;
import exceptions.ConfigException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;


public class ConfigReader {
    private static final Path path;
    static {
        Path rootPath = Paths.get(System.getProperty("user.dir")).getParent();
        path = Paths.get(rootPath.toString(), "config.json");
    }

    public VehicleInformation getVehicleInformationFromConfig(UUID vehicleId) {
        try {
            String jsonString = new String((Files.readAllBytes(path)));
            JSONObject root = new JSONObject(jsonString);

            JSONObject ms = root.getJSONObject(vehicleId.toString());

            assert (ms.getString("nodeType").equals("vehicle"));
            String sVehicleType = ms.getString("vehicleType");
            VehicleType vehicleType = VehicleType.valueOf(sVehicleType);

            JSONArray neighbours = ms.getJSONArray("staticNeighbours");
            assert (neighbours.length() == 1);
            JSONObject neighbour = neighbours.getJSONObject(0);
            assert(neighbour.getString("nodeType").equals("hub"));
            UUID motherHub = UUID.fromString(neighbour.getString("uuid"));

            VehicleInformation vehicleInformation = new VehicleInformation(vehicleId, motherHub, vehicleType);
            return vehicleInformation;

        } catch (IOException e) {
            throw new ConfigException(e);
        }
    }
}
