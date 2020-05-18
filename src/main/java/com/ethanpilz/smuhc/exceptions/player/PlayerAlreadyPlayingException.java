package com.ethanpilz.smuhc.exceptions.player;

public class PlayerAlreadyPlayingException extends Exception {

    public PlayerAlreadyPlayingException() {}

    // Constructor that accepts a message
    public PlayerAlreadyPlayingException(String message) {
        super(message);
    }
}
