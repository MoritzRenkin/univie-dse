package network.mM;

import exceptions.ConfigException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class JsonConvert {

	private static Logger logger = LoggerFactory.getLogger(JsonConvert.class);
	private Path path;
	private UUID id;
	private int port;
	private boolean bridgingActivated;
	private List<NetworkNode> staticNeighbours;
	private NetworkConfig networkConfig;
	private String nodeType;

	public JsonConvert(UUID id) {
		this.id = id;
		Path rootPath = Paths.get(System.getProperty("user.dir")).getParent();
		this.path = Paths.get(rootPath.toString(), "config.json");
	}

	public NetworkConfig getConf() {

		try {

			logger.debug("Converter started");
			String contents = new String((Files.readAllBytes(path)));
			JSONObject o = new JSONObject(contents);

			JSONObject value = o.getJSONObject(id.toString());
			logger.debug(value.toString());

			this.port = value.getInt("port");
			logger.debug("Port is: " + port);

			this.bridgingActivated = value.getBoolean("bridgingActivated");
			logger.debug("Bridging is: " + bridgingActivated);

			this.nodeType = value.getString("nodeType");
			logger.debug("nodeType is: " + nodeType);

			JSONArray neighbours = value.getJSONArray("staticNeighbours");
			List<NetworkNode> staticNeighbours2 = new ArrayList<>();

			for (int i = 0; i < neighbours.length(); i++) {

				String id_neighbours = neighbours.getJSONObject(i).getString("uuid");

				JSONObject o_neighbours = o.getJSONObject(id_neighbours);

				int port_neigbours = o_neighbours.getInt("port");

				InetAddress ip_neigbours = InetAddress.getByName(o_neighbours.getString("ip"));

				logger.debug("Ip: " + ip_neigbours.toString());

				String nodeType_neigbours = o_neighbours.getString("nodeType");

				NetworkNode buff = new NetworkNode(ip_neigbours, port_neigbours, nodeType_neigbours);
				staticNeighbours2.add(buff);

			}

			List<NetworkNode> staticNeighbours;
			staticNeighbours = Collections.unmodifiableList(staticNeighbours2);

			logger.debug("Neighbours are: " + staticNeighbours); // check if works
			networkConfig = new NetworkConfig(staticNeighbours, port, bridgingActivated);

		} catch (IOException e) {
			logger.error("JsonConvert error IOException");
			throw new ConfigException(e);
		} catch (JSONException e) {
			logger.error("JsonConvert error JSONException");

			throw new ConfigException(e);
		}

		return networkConfig;
	}

	public UUID getId() {
		return id;
	}

	public int getPort() {
		return port;
	}

	public boolean isBridgingActivated() {
		return bridgingActivated;
	}

	public String getNodeType() {
		return nodeType;
	}

}
