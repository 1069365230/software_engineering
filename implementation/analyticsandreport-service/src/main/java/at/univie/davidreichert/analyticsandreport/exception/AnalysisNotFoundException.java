package at.univie.davidreichert.analyticsandreport.exception;

public class AnalysisNotFoundException extends RuntimeException {

    public AnalysisNotFoundException(String message) {
        super(message);
    }

    public AnalysisNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}