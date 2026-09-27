package com.example.wiki.error;

public class AlreadyExistsException extends RuntimeException {
    public <T> AlreadyExistsException(Class<T> entClass, String name) {
        super(String.format("%s with name %s is already exists", entClass.getSimpleName(), name));
    }

    public <T> AlreadyExistsException(Class<T> entClass, Long id1, Long id2) {
        super(String.format("%s with id [%s, %s] is already exists", entClass.getSimpleName(), id1, id2));
    }
}
