package main;

import main.database.DatabaseController;
import main.localnetwork.NetworkController;
import main.properties.NetworkEntity;
import main.properties.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.UUID;

@SpringBootApplication
public class MainController {

    public static UUID HUB_OPERATOR_ID = Properties.getHubOperator().getId();
    public static NetworkEntity HUB_OPERATOR = Properties.getHubOperator();
    private final Logger logger = LoggerFactory.getLogger(MainController.class);

    @Autowired
    private NetworkController networkController;

    @Autowired
    private DatabaseController databaseController;

    public MainController() {
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(MainController.class);
        app.setDefaultProperties(Collections.singletonMap("server.port", "" + (HUB_OPERATOR.getPort() + 1000)));
        app.run(args);
    }

    @PostConstruct
    public void init() {
        networkController.receiveInstanceOnlineMessages();
        networkController.sendStartSignal();
        networkController.receiveHubMessages();
        networkController.receiveVehicleMessages();
        logger.info("Subscribed to messages");
    }
}
