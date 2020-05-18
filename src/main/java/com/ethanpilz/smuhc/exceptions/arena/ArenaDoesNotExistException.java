package com.ethanpilz.smuhc.exceptions.arena;

public class ArenaDoesNotExistException extends Exception {
    public ArenaDoesNotExistException() {}

    // Constructor that accepts a message
    public ArenaDoesNotExistException(String message) {
        super(message);
    }
}