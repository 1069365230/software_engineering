package com.ase.recommenderservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NoEventsAvailableException extends RequestException {

    public NoEventsAvailableException(String errorMessage) {
        super(errorMessage, HttpStatus.NOT_FOUND);
    }
}
