package main.database.exceptions;

public class HubNotFoundException extends RuntimeException {
    public HubNotFoundException() {
        super("No HubState with this ID was found!");
    }
}
