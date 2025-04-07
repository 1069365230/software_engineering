package com.ase.attendanceservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ResourceAlreadyExistsException extends RequestException {

    public ResourceAlreadyExistsException(String errorMessage) {
        super(errorMessage, HttpStatus.BAD_REQUEST);
    }

}
