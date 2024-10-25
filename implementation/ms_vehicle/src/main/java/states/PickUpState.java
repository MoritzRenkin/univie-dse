package states;

import callbacks.PickUpCallback;
import data.Container;
import data.Converter;
import messageUtil.ContainerInformation;
import messages.ContainerHandover;
import messages.ContainerPickUpRequest;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class PickUpState extends State{

    private final UUID stationArrived;
    private final Collection<Container> containersToPickUp;
    private final AtomicBoolean handoverComplete = new AtomicBoolean(false);
    private final int distanceToMotherHub;

    public PickUpState(UUID stationArrived, Collection<Container> containersToPickUp, int distanceToMotherHub) {
        this.stationArrived = stationArrived;
        this.containersToPickUp = containersToPickUp;
        this.distanceToMotherHub = distanceToMotherHub;

        assert(!stationArrived.equals(getVehicleInformation().getMotherHubId()));

        Collection<ContainerInformation> convertedContainers = containersToPickUp
                .stream()
                .map(con -> Converter.getRemoteContainerInformation(con, getVehicleInformation().getId()))
                .collect(Collectors.toList());

        publisher.publish(new ContainerPickUpRequest(getVehicleInformation().getId(), stationArrived, convertedContainers));
        subscriber.addSubscription(ContainerHandover.class, new PickUpCallback(containersToPickUp, handoverComplete));
    }


    @Override
    public State proceed() {
        if (!handoverComplete.get()) {
            return this;
        }
        subscriber.removeSubscription(ContainerHandover.class);

        return JourneyState.Builder()
                .withSource(stationArrived)
                .withDestination(getVehicleInformation().getMotherHubId())
                .withTotalDistance(distanceToMotherHub)
                .withContainersContained(containersToPickUp)
                .setPickup(false)
                .build();
    }

    @Override
    public String toString() {
        return "PickUpState{" +
                "stationArrived=" + stationArrived +
                ", containersToPickUp=" + containersToPickUp +
                ", handoverComplete=" + handoverComplete +
                ", distanceToMotherHub=" + distanceToMotherHub +
                '}';
    }
}
