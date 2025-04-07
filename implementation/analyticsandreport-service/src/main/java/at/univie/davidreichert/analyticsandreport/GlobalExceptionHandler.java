package at.univie.davidreichert.analyticsandreport;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Void> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<Void> handleMissingPathVariableException(MissingPathVariableException e) {
        return ResponseEntity.badRequest().build();

    }

}