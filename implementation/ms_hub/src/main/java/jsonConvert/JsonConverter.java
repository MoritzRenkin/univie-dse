package jsonConvert;

import map.Location;
import map.LocationConnection;
import map.MS_Type;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import transport.EVehicleType;
import transport.Vehicle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class JsonConverter {
    private final UUID myUUID;
    private static final Logger logger = LoggerFactory.getLogger(JsonConverter.class);
    private final Path path;

    public JsonConverter(UUID myUUID) {
        this.myUUID = myUUID;
        Path rootPath = Paths.get(System.getProperty("user.dir")).getParent();
        this.path = Paths.get(rootPath.toString(), "config.json");

    }

    public StartUpInfo convert() {

        StartUpInfo startUpInfo = null;

        try {

            File file = new File(String.valueOf(path));
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader(file));
            JSONObject jsonObject = (JSONObject) obj;

            JSONObject myHubObj = (JSONObject) jsonObject.get(myUUID.toString());

            final long hubCapacity = (long) myHubObj.get("capacity");
            logger.debug("HubCapacity: {}", hubCapacity);

            JSONArray neighbours = (JSONArray) myHubObj.get("staticNeighbours");
            Set<LocationConnection> neighbourConnections = new HashSet<>();
            Set<Vehicle> assignedVehicles = new HashSet<>();
            Location myLocation = new Location(myUUID, MS_Type.HUB);

            for (Object o : neighbours) {
                JSONObject neighbourObj = (JSONObject) o;
                String nodeType = (String) neighbourObj.get("nodeType");
                Location neighbourLocation;
                String uuid = (String) neighbourObj.get("uuid");
                long distance;
                switch (nodeType) {
                    case "hub":
                        distance = (long) neighbourObj.get("distance");
                        neighbourLocation = new Location(UUID.fromString(uuid), MS_Type.HUB);
                        neighbourConnections.add(new LocationConnection(myLocation, neighbourLocation, (int) distance));
                        break;
                    case "station":
                        distance = (long) neighbourObj.get("distance");
                        neighbourLocation = new Location(UUID.fromString(uuid), MS_Type.STATION);
                        neighbourConnections.add(new LocationConnection(myLocation, neighbourLocation, (int) distance));
                        break;
                    case "vehicle":
                        assignedVehicles.add(new Vehicle(UUID.fromString(uuid)));
                        break;
                }
            }

            //set vehicleTypes
            for (var vehicle : assignedVehicles) {
                JSONObject vehicleJSON = (JSONObject) jsonObject.get(vehicle.getVehicleID().toString());
                vehicle.setVehicleType(EVehicleType.valueOf((String) vehicleJSON.get("vehicleType")));
            }

            for (var vehicle : assignedVehicles) {
                logger.debug("vehicle connection added:"+vehicle.toString());
            }

            for (var neighbour : neighbourConnections) {
                logger.debug("neighbour connection added:"+neighbour.toString());
            }

            final int numberOfHubs = countNumberOfHubs(jsonObject);
            logger.debug("number of Hubs: {}", numberOfHubs);

            startUpInfo = new StartUpInfo(neighbourConnections, assignedVehicles, (int) hubCapacity, numberOfHubs);

        } catch (FileNotFoundException e) {
            logger.error("JsonConverter error FileNotFoundException");
        } catch (IOException e) {
            logger.error("JsonConverter error IOException");
        } catch (ParseException e) {
            logger.error("JsonConverter error ParseException");
        }

        return startUpInfo;

    }

    private int countNumberOfHubs(JSONObject obj) {
        int numberOfHubs = 0;
        var keys = obj.keySet();
        for (var key : keys) {
            JSONObject value = (JSONObject) obj.get(key);
            if (value.get("nodeType").equals("hub")) {
                ++numberOfHubs;
            }
        }
        return numberOfHubs;
    }

}
