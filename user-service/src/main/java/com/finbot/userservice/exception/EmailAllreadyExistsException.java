package com.finbot.userservice.exception;

public class EmailAllreadyExistsException extends RuntimeException {
    public EmailAllreadyExistsException(String message) {
        super(message);
    }
}
