package main.database.exceptions;

public class ContainerNotFoundException extends RuntimeException {
    public ContainerNotFoundException() {
        super("No ContainerState with this ID was found!");
    }
}
