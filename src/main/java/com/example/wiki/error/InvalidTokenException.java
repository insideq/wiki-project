package com.example.wiki.error;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException() {
        super("Invalid token");
    }
}
