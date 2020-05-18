package com.ethanpilz.smuhc.exceptions.game;

public class GameInProgressException extends Exception {

    public GameInProgressException() {}

    // Constructor that accepts a message
    public GameInProgressException(String message) {
        super(message);
    }
}