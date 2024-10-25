package main;


import main.localnetwork.NetworkController;
import main.objects.Container;
import main.objects.ContainerRepository;
import main.properties.NetworkEntity;
import main.properties.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Random;
import java.util.UUID;

@SpringBootApplication
public class MainController {

    private NetworkController networkController;
    public static UUID STATION_ID;
    public static NetworkEntity STATION;
    public static int ADDED_TO_PORT = 1000;
    public MainController() {
    }

    public static void main(String[] args) {
        STATION = Properties.getStation(UUID.fromString(args[0]));
        STATION_ID = STATION.getId();
        SpringApplication app = new SpringApplication(MainController.class);
        app.setDefaultProperties(Collections.singletonMap("server.port", "" + (STATION.getPort() + ADDED_TO_PORT)));
        System.out.println("SERVER: http://localhost:" + (STATION.getPort() + ADDED_TO_PORT));
        app.run(args);
    }

    @PostConstruct
    public void init(){
        networkController.publishInstanceOnline();
        networkController.addNewContainerListener();
        networkController.addContainerOnFinalDestinationListener();
        networkController.subscribeToContainerPickUpRequest();
        networkController.subscribeToContainerHandover();
        networkController.subscribeToVehicleArrival();
        /*var repo = ContainerRepository.getInstance();
        Random rand = new Random();
        for (int i = 0; i < 20; i++) {
            repo.addContainerToPickup(new Container(UUID.randomUUID(),rand.nextInt(15)+1, 1233));
            repo.addContainerToSent(new Container(UUID.randomUUID(),rand.nextInt(15)+1,32332));
        }*/
    }

    @Autowired
    public void setNetworkController(NetworkController networkController) {
        this.networkController = networkController;
    }
}
