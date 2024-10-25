package main.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


@Repository
interface ContainerRepository extends JpaRepository<ContainerState, Integer> {

    @Query("select c from ContainerState c where c.containerId = :id")
    ContainerState getContainer(@Param("id") UUID id);

    @Query("select c from ContainerState c where c.vehicleId = :vehicleId and c.distanceToGo <> 0")
    List<ContainerState> getLatestContainerStateByVehicle(@Param("vehicleId") UUID vehicleId);
}

@Repository
interface HubRepository extends JpaRepository<HubState, Integer> {

    @Query("select h from HubState h where h.hubId = :id")
    HubState getHub(@Param("id") UUID id);

}

@Repository
interface ContainerHistoryRepository extends JpaRepository<ContainerHistoryState, Integer> {

    @Query("select ch from ContainerHistoryState ch where ch.containerId = :id")
    List<ContainerHistoryState> getContainerHistory(@Param("id") UUID id);
}


