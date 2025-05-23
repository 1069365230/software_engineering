package com.ase.attendanceservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class RequestException extends RuntimeException {
    private LocalDateTime timestamp;
    private HttpStatus httpStatus;

    public RequestException(String errorMessage, HttpStatus httpStatus) {
        super(errorMessage);
        this.httpStatus = httpStatus;
        this.timestamp = LocalDateTime.now();
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
