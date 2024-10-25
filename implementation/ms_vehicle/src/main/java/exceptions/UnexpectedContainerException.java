package exceptions;

public class UnexpectedContainerException extends RuntimeException{
    public UnexpectedContainerException() {
    }

    public UnexpectedContainerException(String message) {
        super(message);
    }

    public UnexpectedContainerException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnexpectedContainerException(Throwable cause) {
        super(cause);
    }
}
