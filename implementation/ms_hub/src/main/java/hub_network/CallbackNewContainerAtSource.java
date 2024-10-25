package hub_network;

import messageUtil.ContainerInformation;
import messages.NewContainerAtSource;
import network.Callback;
import transport.Container;

import java.util.Queue;


public class CallbackNewContainerAtSource implements Callback<NewContainerAtSource> {
    private final Queue<Container> newContainerAtSourceQueue;

    public CallbackNewContainerAtSource(Queue<Container> newContainerAtSourceQueue) {
        this.newContainerAtSourceQueue = newContainerAtSourceQueue;
    }

    @Override
    public void onResponse(NewContainerAtSource message) {
        ContainerInformation cInfo = message.getContainerInformation();
        newContainerAtSourceQueue.add(new Container(cInfo.getContainerId(), cInfo.getWeight(),
                cInfo.getSourceStation(), cInfo.getDestinationStation()));
    }
}
