package main.database.exceptions;

public class ContainerHistoryNotFoundException extends RuntimeException {
    public ContainerHistoryNotFoundException() {
        super("No ContainerState History with this ID was found!");
    }
}
