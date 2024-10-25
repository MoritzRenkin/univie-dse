package main.localnetwork.exceptions;

public class StationNotFoundException extends RuntimeException {
    public StationNotFoundException() {
        super("The Station was not found!");
    }
}
