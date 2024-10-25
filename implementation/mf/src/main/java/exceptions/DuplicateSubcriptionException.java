package exceptions;

public class DuplicateSubcriptionException extends RuntimeException {

	public DuplicateSubcriptionException() {
		super();
	}

	public DuplicateSubcriptionException(String message) {
		super(message);
	}
}
