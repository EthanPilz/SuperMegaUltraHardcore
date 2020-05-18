package com.ethanpilz.smuhc.exceptions.player;

public class PlayerNotPlayingException extends Exception {

    public PlayerNotPlayingException() {}

    // Constructor that accepts a message
    public PlayerNotPlayingException(String message) {
        super(message);
    }
}