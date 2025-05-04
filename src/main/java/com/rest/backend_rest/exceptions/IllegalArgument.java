package com.rest.backend_rest.exceptions;

public class IllegalArgument extends RuntimeException {
    public IllegalArgument(String message) {
        super(message);
    }
}
