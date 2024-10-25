package transport;

public enum EVehicleType {ROBOT(50), BIKE(30), CAR(200), VAN(1000);
    int capacity;


    EVehicleType(int capacity) {
        this.capacity = capacity;
    }

    public int getCapacity() {
        return capacity;
    }

}

