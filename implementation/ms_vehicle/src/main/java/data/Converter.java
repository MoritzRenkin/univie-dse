package data;

import messageUtil.ContainerInformation;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Converter {
    public static Container getLocalContainer(ContainerInformation remoteContainerInformation) {
        Container ret = new Container(
                remoteContainerInformation.getContainerId(),
                remoteContainerInformation.getWeight(),
                remoteContainerInformation.getSourceStation(),
                remoteContainerInformation.getDestinationStation());

        return ret;
    }

    public static ContainerInformation getRemoteContainerInformation(Container container, UUID currentHolder) {
        ContainerInformation ret = new ContainerInformation(container.getId(),
                container.getWeight(), container.getSourceStation(), container.getDestinationStation(), currentHolder);

        return ret;
    }

    public static List<ContainerInformation> getAllRemoteContainerInformation(Collection<Container> containers, UUID currentHolder) {
        return containers
                .stream()
                .map(container -> Converter.getRemoteContainerInformation(container, currentHolder))
                .collect(Collectors.toList());
    }
}
