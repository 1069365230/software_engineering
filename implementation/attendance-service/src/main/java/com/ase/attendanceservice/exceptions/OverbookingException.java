package com.ase.attendanceservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class OverbookingException extends RequestException {

    public OverbookingException(String errorMessage) {
        super(errorMessage, HttpStatus.CONFLICT);
    }
}
