package main.localnetwork.exceptions;

public class HubOperatorNotFoundException extends RuntimeException {
    public HubOperatorNotFoundException() {
        super("The Hub Operator was not found!");
    }
}
