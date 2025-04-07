package at.univie.davidreichert.feedbackservice.exceptions;

public class AttendeeNotAttendingException extends RuntimeException {
    public AttendeeNotAttendingException(String message) {
        super(message);
    }

    public AttendeeNotAttendingException(String message, Throwable cause) {
        super(message, cause);
    }
}