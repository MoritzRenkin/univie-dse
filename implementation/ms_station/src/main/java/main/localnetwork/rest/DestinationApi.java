package main.localnetwork.rest;


import main.objects.ContainerRepository;
import main.objects.Container;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@RestController
public class DestinationApi {

    private final ContainerRepository containerRep = ContainerRepository.getInstance();
    private final String SERVER = "http://localhost:4201";

    @CrossOrigin(origins = SERVER)
    @GetMapping("/destination/{containerId}")
    public ContainerReply getSingleDestinationContainer(@PathVariable String containerId) {
        return new ContainerReply(containerRep.getContainerToPickup(UUID.fromString(containerId)));
    }

    @CrossOrigin(origins = SERVER)
    @GetMapping("/destination/all-containers")
    public List<ContainerReply> getDestinationContainers() {
        return containerRep.getAllContainerToPickup()
                .stream()
                .map(ContainerReply::new)
                .collect(toList());
    }

    @CrossOrigin(origins = SERVER)
    @DeleteMapping("/destination/pickup/{containerId}")
    public boolean pickupContainer(@PathVariable String containerId) {
        //this.containerRep.destroyContainer(UUID.fromString(containerId));
        return containerRep.removeContainerToPickup(UUID.fromString(containerId)).isPresent();
    }
}


class ExtendedContainer {
    private final Container container;
    private final String link;

    public ExtendedContainer(Container container, String link) {
        this.container = container;
        this.link = link;
    }

    public Container getContainer() {
        return container;
    }

    public String getLink() {
        return link;
    }
}
