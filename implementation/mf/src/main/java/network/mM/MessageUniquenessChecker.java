package network.mM;

import messages.AbstractMessage;

import java.util.*;

class MessageUniquenessChecker {
	private static final int MAX_SAVED_UUIDS = 1000;
	private Set<UUID> receivedUUIDs = new HashSet<>();
	private Queue<UUID> deleteQueue = new LinkedList<>(); //this creates Redundancy, but greatly accelerates the execution of deleting old messages

	private void receiveUUID(UUID uuid) {
		receivedUUIDs.add(uuid);
		deleteQueue.add(uuid);

		if (deleteQueue.size() > MAX_SAVED_UUIDS) {
			UUID uuidToDelete = deleteQueue.poll();
			receivedUUIDs.remove(uuidToDelete);
		}
	}

	public boolean isMessageUnique(AbstractMessage msg) {
		UUID msgUUID = msg.getMessageUUID();
		if (receivedUUIDs.contains(msgUUID)) {
			return false;
		}
		receiveUUID(msgUUID);
		return true;
	}
}
