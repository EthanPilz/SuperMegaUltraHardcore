package com.ethanpilz.smuhc.exceptions.game;

public class GameFullException extends Exception {
    public GameFullException() {}

    // Constructor that accepts a message
    public GameFullException(String message)
    {
        super(message);
    }
}
