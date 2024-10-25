package states;

import java.util.Date;
import org.junit.*;
import static org.junit.Assert.*;

public class DistanceCalculatorTest {

	private final int TOTAL_DISTANCE = 100; //km
	private final int SPEED = 20; //kmh
	private final double SPEEDUP_FACTOR = DistanceCalculator.getSpeedupFactor();

	private DistanceCalculator distanceCalculator;

	@Before
	public void renewCalculator() {
		distanceCalculator = new DistanceCalculator(TOTAL_DISTANCE, SPEED);
	}

	@Test
	public void notMoved_moveAfterToSeconds_coveredCorrectDistance() throws InterruptedException {
		assertEquals(TOTAL_DISTANCE, distanceCalculator.getTotalDistance());

		double hoursPassed = 0.001; // == 3.6 sec
		double secondsPassed = hoursPassed * 3600;
		System.out.println("Seconds passed:" + secondsPassed);
		double expectedDistanceMoved = SPEED * hoursPassed * SPEEDUP_FACTOR;

		long lastMoved = distanceCalculator.getLastMoved();
		long moveAt = (long) (lastMoved + (secondsPassed * 1000));

		while (new Date().getTime() < moveAt) {
			Thread.sleep(10);
		}

		distanceCalculator.move();
		double distanceMoved = TOTAL_DISTANCE - distanceCalculator.getRemainingDistance();

		double allowedErrorFactor = 0.01;
		double errorFactor = 1 - Math.min(distanceMoved, expectedDistanceMoved) / Math.max(distanceMoved, expectedDistanceMoved);
		assertTrue("Error Factor: " + errorFactor + " ", errorFactor < allowedErrorFactor);
	}
}
