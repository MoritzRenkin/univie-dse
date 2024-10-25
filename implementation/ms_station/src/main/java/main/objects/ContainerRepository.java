package main.objects;


import main.localnetwork.exceptions.ContainerNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;

@Service
public class ContainerRepository {
    private final List<Container> containersToSent;
    private final List<Container> containersToPickup;
    private final Map<UUID, Process> containerInstances;
    private static ContainerRepository instance;
    private final PropertyChangeSupport changeSupport;
    public static final String CONTAINER_TO_SEND = "containerToSent";
    public static final String CONTAINER_TO_PICKUP = "containerToPickup";
    private Logger logger = LoggerFactory.getLogger(ContainerRepository.class);

    private ContainerRepository() {
        containerInstances = new HashMap<>();
        containersToSent = new ArrayList<>();
        containersToPickup = new ArrayList<>();
        changeSupport = new PropertyChangeSupport(this);
    }

    public static ContainerRepository getInstance() {
        if (instance == null) {
            return instance = new ContainerRepository();
        }
        return instance;
    }

    public void addListener(PropertyChangeListener listener){
        this.changeSupport.addPropertyChangeListener(listener);
    }

    public void addContainerToSent(Container container) {
        containersToSent.add(container);
        changeSupport.firePropertyChange(new PropertyChangeEvent(this, CONTAINER_TO_SEND, null, container));
    }

    public Optional<Container> removeContainerToSent(UUID container) {
        var temp = containersToSent.stream().filter(cont -> cont.getId().equals(container)).findFirst();
        containersToSent.removeIf(cont -> cont.getId().equals(container));
        return temp;
    }

    public void addContainerToPickup(Container container) {
        containersToPickup.add(container);
        changeSupport.firePropertyChange(new PropertyChangeEvent(this, CONTAINER_TO_PICKUP, null, container));
    }

    public Optional<Container> removeContainerToPickup(UUID container) {
        var temp = containersToPickup.stream().filter(cont -> cont.getId().equals(container)).findFirst();
        containersToPickup.removeIf(cont -> cont.getId().equals(container));
        return temp;
    }

    public Container getContainerToSent(UUID id) {
        return containersToSent.stream()
                .filter(container -> container.getId().equals(id))
                .findFirst()
                .orElseThrow(ContainerNotFoundException::new);
    }


    public List<Container> getAllContainersToSent() {
        return this.containersToSent;
    }

    public void addAllContainerSent(List<Container> containers) {
        for(var cont: containers){
            this.addContainerToSent(cont);
        }
    }

    public Container getContainerToPickup(UUID uuid) {
        return containersToPickup.stream()
                .filter(container -> container.getId().equals(uuid))
                .findFirst()
                .orElseThrow(ContainerNotFoundException::new);
    }

    public List<Container> getAllContainerToPickup() {
        return containersToPickup;
    }

    public void addContainerInstance(UUID id, Process process){
        if(process == null){
            logger.info("Container Process was null!");
        }
        this.containerInstances.put(id, process);
    }

    public void destroyContainer(UUID id){
        var result = this.containerInstances.get(id);
        if(result != null) {
            if (result.isAlive()) {
                result.destroy();
            }
        }
        this.containerInstances.remove(id);
    }

    public void destroyAllContainers() {
        this.containerInstances.forEach((id, proc) -> proc.destroy());
    }
}
