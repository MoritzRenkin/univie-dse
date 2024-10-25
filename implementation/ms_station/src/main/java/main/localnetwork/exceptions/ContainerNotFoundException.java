package main.localnetwork.exceptions;

public class ContainerNotFoundException extends RuntimeException {
    public ContainerNotFoundException() {
        super("No container found!");
    }
}
