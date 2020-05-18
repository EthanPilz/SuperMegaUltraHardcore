package com.ethanpilz.smuhc.exceptions.arena;

public class ArenaAlreadyExistsException extends Exception {
    public ArenaAlreadyExistsException() {}

    // Constructor that accepts a message
    public ArenaAlreadyExistsException(String message) {
        super(message);
    }
}