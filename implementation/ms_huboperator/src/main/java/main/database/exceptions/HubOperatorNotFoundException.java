package main.database.exceptions;

public class HubOperatorNotFoundException extends RuntimeException {
    public HubOperatorNotFoundException() {
        super("No Hub Operator with this ID was found!");
    }
}
