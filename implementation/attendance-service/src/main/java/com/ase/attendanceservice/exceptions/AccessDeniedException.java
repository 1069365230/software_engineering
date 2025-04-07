package com.ase.attendanceservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class AccessDeniedException extends RequestException {

    public AccessDeniedException(String errorMessage) {
        super(errorMessage, HttpStatus.FORBIDDEN);
    }
}