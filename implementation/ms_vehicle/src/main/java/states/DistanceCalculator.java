package states;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DistanceCalculator {
	private static Logger logger = LoggerFactory.getLogger(DistanceCalculator.class);
	
	private final int totalDistance; // in km
	private double remainingDistance; 
	private final double speed; // converted to km/s
	private long lastMoved;
	static final float SPEEDUP_FACTOR = 40;

	/**
	 * @param totalDistance in km
	 * @param speed in km/h
	 */
	public DistanceCalculator(int totalDistance, int speed) {
		super();
		this.totalDistance = totalDistance;
		this.remainingDistance = totalDistance; 
		//Converting km/h to km/s
		this.speed = speed / 3600.0;
		this.lastMoved = new Date().getTime();
	}

	/**
	 * Calculates the remainingDistance based on speed and the time passed since move() was last called.
	 * This way it is invariant to different refresh times. (It does not react to a different STATE_REFRESH_TIME in Controller)
	 */
	public void move() {
		long currTime = new Date().getTime();
		double secondsPassed =  (currTime - lastMoved) / 1000.0;
		
		double distanceCovered = speed * secondsPassed * SPEEDUP_FACTOR;
		
		remainingDistance -= distanceCovered;
		lastMoved = currTime;
	}

	public double getRemainingDistance() {
		return remainingDistance;
	}

	public int getTotalDistance() {
		return totalDistance;
	}

	public double getSpeed() {
		return speed;
	}

	public static float getSpeedupFactor() {
		return SPEEDUP_FACTOR;
	}

	public long getLastMoved() {
		return lastMoved;
	}

	@Override
	public String toString() {
		return "DistanceCalculator{" +
				"totalDistance=" + totalDistance +
				", remainingDistance=" + remainingDistance +
				", speed=" + speed +
				", lastMoved=" + lastMoved +
				'}';
	}
}
