package network.mM;

import messages.AbstractMessage;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MessageUniquenessCheckerTest {

	private static final int INITIAL_MESSAGES_AMOUNT = 100;
	private final List<AbstractMessage> initialMessages;

	public MessageUniquenessCheckerTest() {
		super();
		initialMessages = new ArrayList<>();
		for(int i=0; i<INITIAL_MESSAGES_AMOUNT; ++i) {
			initialMessages.add(new AbstractMessage(UUID.randomUUID()) {
			});
		}
	}

	private MessageUniquenessChecker getUniquenessCheckerWithFewMessages() {
		MessageUniquenessChecker ret = new MessageUniquenessChecker();
		for (AbstractMessage eachMessage: initialMessages) {
			ret.isMessageUnique(eachMessage);
		}
		return ret;
	}


	@Test
	public void receivedFewMessages_callIsUniqueForReceivedMessages_returnsFalse() {
		MessageUniquenessChecker checker = getUniquenessCheckerWithFewMessages();

		for (AbstractMessage eachMessage: initialMessages) {
			assertFalse(checker.isMessageUnique(eachMessage));
		}
	}

	@Test(timeout = 2000)
	public void receivedTooManyMessages_callIsUniqueForRecentlyReceivedMessages_returnsFalse() {
		final int receivedMessagesAmount = 100000;
		final int testedMessages = 250;

		MessageUniquenessChecker checker = new MessageUniquenessChecker();

		List<AbstractMessage> receivedMessages = new ArrayList<>();
		for (int i = 0; i < receivedMessagesAmount; ++i) {
			AbstractMessage currMessage = new AbstractMessage(UUID.randomUUID()) {
			};
			receivedMessages.add(currMessage);
			checker.isMessageUnique(currMessage);
		}

		for (int i = 0; i < testedMessages; ++i) {
			int currIdx = receivedMessagesAmount - 1 - i;
			AbstractMessage currMessage = receivedMessages.get(currIdx);
			assertFalse("" + i + " Messages received after this message", checker.isMessageUnique(currMessage));
		}
	}

	@Test(timeout = 2000)
	public void receivedFewMessages_callIsUniqueForNewMessages_returnsTrue() {
		final int testsAmount = 100000;

		MessageUniquenessChecker checker = getUniquenessCheckerWithFewMessages();

		for (int i=0; i<testsAmount; ++i) {
			AbstractMessage currMessage = new AbstractMessage(UUID.randomUUID()) {
			};
			assertTrue(checker.isMessageUnique(currMessage));
		}
	}
}
