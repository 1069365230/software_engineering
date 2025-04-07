package com.jin.service.marktagservice.exception;

public class TagAlreadyExistsException extends IllegalArgumentException{
    public TagAlreadyExistsException(String s) {
        super(s);
    }
}
