package main.database;

import main.database.exceptions.ContainerHistoryNotFoundException;
import main.database.exceptions.ContainerNotFoundException;
import main.database.exceptions.HubNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class DatabaseController {

    @Autowired
    private ContainerRepository containerRepo;
    @Autowired
    private ContainerHistoryRepository containerHistoryRepo;
    @Autowired
    private HubRepository hubRepo;

    public DatabaseController() {
    }


    public synchronized void addContainer(ContainerState containerState) {
        this.containerRepo.save(containerState);
    }

    public synchronized void addHub(HubState hubState) {
        this.hubRepo.save(hubState);
    }

    public List<HubState> getAllHubs() {
        return hubRepo.findAll();
    }

    public List<ContainerState> getAllContainers() {
        return containerRepo.findAll();
    }

    public List<ContainerHistoryState> getAllContainerHistories() {
        return containerHistoryRepo.findAll();
    }

    public HubState getHubState(UUID id) {
        var result = this.hubRepo.getHub(id);
        if (result != null) return result;
        throw new HubNotFoundException();
    }

    public HubState getHubState(int id) {
        return this.hubRepo.findById(id).orElseThrow(HubNotFoundException::new);
    }

    public ContainerState getContainerState(int id) {
        return this.containerRepo.findById(id).orElseThrow(ContainerNotFoundException::new);
    }

    public ContainerState getContainerState(UUID id) {
        var result = this.containerRepo.getContainer(id);
        if (result != null) return result;
        throw new ContainerNotFoundException();
    }

    public ContainerHistoryState getContainerHistoryState(int id) {
        return this.containerHistoryRepo.findById(id).orElseThrow(ContainerHistoryNotFoundException::new);
    }

    public List<ContainerHistoryState> getContainerHistoryState(UUID id) {
        var result = this.containerHistoryRepo.getContainerHistory(id);
        if (result != null) return result;
        throw new ContainerNotFoundException();
    }

    public synchronized void addContainerHistoryState(ContainerHistoryState containerHistoryState) {
        this.containerHistoryRepo.save(containerHistoryState);
    }

    public List<ContainerState> getContainersOfVehicle(UUID vehicleId) {
        return this.containerRepo.getLatestContainerStateByVehicle(vehicleId);
    }
}
