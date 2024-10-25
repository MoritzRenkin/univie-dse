package integrationTests;

import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.junit.*;
import static org.junit.Assert.*;

import network.NetworkServiceFactory;

public class BasicTests {
	private UUID uuid = UUID.fromString("61297ae3-b7bf-474a-b839-7256d63b06c6");

	@Test
	public void notInitialized_initialize_doesNotThrow() throws InterruptedException {
		NetworkServiceFactory.initialize(uuid);
		
		Thread.sleep(1000);
		
		boolean initialized = NetworkServiceFactory.isInitialized();
		
		assertTrue(initialized);
	}

	//@Test
	public void initialized_callTerminate_AllThreadsExited() throws InterruptedException {
		NetworkServiceFactory.initialize(uuid);
		Thread.sleep(500);

		System.out.println(Thread.getAllStackTraces().keySet().size());
		NetworkServiceFactory.terminate();
		Thread.sleep(1000);

		assertEquals(1, Thread.getAllStackTraces().keySet().size());

	}
	
}
