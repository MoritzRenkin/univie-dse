package data;

public enum VehicleType {ROBOT(50, 10), BIKE(30, 45), CAR(200, 80), VAN(1000, 65);
	int capacity;
	int speed;
	
	
	private VehicleType(int capacity, int speed) {
		this.capacity = capacity;
		this.speed = speed; // in km/h
	}

	public int getCapacity() {
		return capacity;
	}

	public int getSpeed() {
		return speed;
	}
}
