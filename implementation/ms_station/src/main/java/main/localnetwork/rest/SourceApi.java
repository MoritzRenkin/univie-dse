package main.localnetwork.rest;

import main.MainController;
import main.objects.Container;
import main.objects.ContainerRepository;
import main.properties.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.SocketUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PreDestroy;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class SourceApi {

    private final ContainerRepository containers = ContainerRepository.getInstance();
    private final String WEB_UI_SERVER = "http://localhost:4201";
    private final Random rand = new Random();
    private final Logger logger = LoggerFactory.getLogger(SourceApi.class);
    //private final List<Thread> outputThreads = new ArrayList<>();

    @CrossOrigin(origins = WEB_UI_SERVER)
    @GetMapping("/source/{containerId}")
    public ContainerReply getSingleSourceContainer(@PathVariable String containerId) {
        return new ContainerReply(containers.getContainerToSent(UUID.fromString(containerId)));
    }

    @CrossOrigin(origins = WEB_UI_SERVER)
    @GetMapping("/source/all-containers")
    public List<ContainerReply> getSourceContainers() {
        return containers.getAllContainersToSent().stream().map(ContainerReply::new).collect(Collectors.toList());
    }

    @CrossOrigin(origins = WEB_UI_SERVER)
    @GetMapping("/source/insert/{destination}/{weight}")
    public boolean createSingleContainer(@PathVariable String destination, @PathVariable int weight) {
        //TODO care about parameters from post request
        var newCont = this.newContainer(UUID.fromString(destination), weight);
        newCont.ifPresent(this.containers::addContainerToSent);
        logger.info("New Container");
        return true;
    }

    @CrossOrigin(origins = WEB_UI_SERVER)
    @GetMapping("/source/insert/bulk/{number}") //TODO: destination maybe
    public boolean createMultipleContainer(@PathVariable String number) {
        List<Container> createdContainers = new ArrayList<>();
        int input = 0;
        try {
            input = Integer.parseInt(number);
        } catch (NumberFormatException e) {
            logger.warn("Invalid Number parsed! input was set to 0");
        }
        for (int i = 0; i < input; i++) {
            var cont = newRandomContainer();
            logger.info("New Container");
            cont.ifPresent(createdContainers::add);
        }
        this.containers.addAllContainerSent(createdContainers);
        return true;
    }

    private Optional<Container> newContainer(UUID destination, int weight) {
        try {
            var uuid = UUID.randomUUID();
            String pathToContainerJar = "/Users/lukasgreiner/Projekte/IdeaProjects/DSE_Team_104/implementation/ms_container/build/libs/";
            int port = SocketUtils.findAvailableTcpPort();
            Process proc = Runtime.getRuntime()
                    .exec(new String[]{"java", "-jar", pathToContainerJar + "ms_container-all.jar",
                            uuid.toString(), String.valueOf(weight), String.valueOf(port),
                            MainController.STATION_ID.toString(), destination.toString()});
            //proc.waitFor();
            if (proc.isAlive()) {
                var in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                var err = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
                //this.printContainerOutput(in, err);
                this.containers.addContainerInstance(uuid, proc);
                logger.info("Container Process was created with UUID: " + uuid);
            } else {
                logger.warn("Container Process was not created!");
            }
            return Optional.of(new Container(destination, weight, port));
        } catch (IOException  e) {
            e.printStackTrace();
            logger.warn("Container Process was not created!");
        }
        return Optional.empty();
    }

    /*private void printContainerOutput(BufferedReader in, BufferedReader err) {
        Runnable errorRunnable = () -> {
            String line;
            StringBuilder error = new StringBuilder();
            try {
                while ((line = err.readLine()) != null) {
                    error.append("\n").append(line);
                }
                logger.error(String.valueOf(error));
                err.close();
            } catch (final IOException ignored) {
            }
        };
        Runnable inputRunnable = () -> {
            String line;
            StringBuilder output = new StringBuilder();
            try {
                while ((line = in.readLine()) != null) {
                    output.append("\n").append(line);
                    logger.info(output.toString());
                    output = new StringBuilder();
                }
            } catch (final IOException ignored) {
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread inputThread = new Thread(inputRunnable);
        Thread errorOutput = new Thread(errorRunnable);
        outputThreads.add(inputThread);
        outputThreads.add(errorOutput);
        inputThread.start();
        errorOutput.start();
    }*/

    private Optional<Container> newRandomContainer() {
        UUID generatedId = Properties.allStations().get(rand.nextInt(Properties.allStations().size()));
        while (MainController.STATION_ID.equals(generatedId)) {
            generatedId = Properties.allStations().get(rand.nextInt(Properties.allStations().size()));
        }
        int weight = rand.nextInt(15) + 1;
        return newContainer(generatedId, weight);
    }

    @PreDestroy
    private void destroyContainers() {
        //this.outputThreads.forEach(Thread::interrupt);
        this.containers.destroyAllContainers();
        logger.info("All containers and their output were destroyed");
    }
}

class ContainerReply {
    private final UUID id;
    private final UUID currentLocation;
    private final UUID destinationLocation;
    private final int weight;

    public ContainerReply(Container container) {
        this.id = container.getId();
        this.currentLocation = container.getCurrentLocation();
        this.destinationLocation = container.getDestinationLocation();
        this.weight = container.getWeight();
    }

    public UUID getId() {
        return id;
    }

    public UUID getCurrentLocation() {
        return currentLocation;
    }

    public UUID getDestinationLocation() {
        return destinationLocation;
    }

    public int getWeight() {
        return weight;
    }
}

