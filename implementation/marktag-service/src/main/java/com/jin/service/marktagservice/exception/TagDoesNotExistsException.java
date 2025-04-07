package com.jin.service.marktagservice.exception;

public class TagDoesNotExistsException extends IllegalArgumentException{
    public TagDoesNotExistsException(String s) {
        super(s);
    }
}
