package exceptions;

public class NoSuchNeighbourException extends RuntimeException {
    public NoSuchNeighbourException() {
    }

    public NoSuchNeighbourException(String message) {
        super(message);
    }

    public NoSuchNeighbourException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchNeighbourException(Throwable cause) {
        super(cause);
    }
}
