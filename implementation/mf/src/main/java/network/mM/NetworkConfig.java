package network.mM;

import java.util.Collections;
import java.util.List;


class NetworkConfig {
	/**
	 * A thread safe data class to configure the MessageFramework. Cannot be changed once initialised.
	 */

	private final int port;
	private final boolean bridgingActivated;
	private final List<NetworkNode> staticNeighbours;


	public NetworkConfig(List<NetworkNode> staticNeighbours, int port, boolean bridgingActivated) {
		super();
		this.bridgingActivated = bridgingActivated;
		this.staticNeighbours = Collections.unmodifiableList(staticNeighbours);
		this.port = port;
	}

	public NetworkConfig(List<NetworkNode> staticNeighbours, int port) {

		this(staticNeighbours, port, true);
	}

	public boolean isBridgingActivated() {
		return bridgingActivated;
	}

	public List<NetworkNode> getStaticNeighbours() {
		return staticNeighbours;
	}

	public int getPort() {
		return port;
	}
}
