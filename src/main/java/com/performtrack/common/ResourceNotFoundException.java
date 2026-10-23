package com.performtrack.common;

public class ResourceNotFoundException
        extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
