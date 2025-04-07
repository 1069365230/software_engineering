package at.univie.davidreichert.feedbackservice.exceptions;

public class AttendeeNotFoundException extends RuntimeException {
    public AttendeeNotFoundException(String message) {
        super(message);
    }

    public AttendeeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}