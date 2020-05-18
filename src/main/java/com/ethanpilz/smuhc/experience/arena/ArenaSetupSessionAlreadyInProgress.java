package com.ethanpilz.smuhc.experience.arena;

public class ArenaSetupSessionAlreadyInProgress extends Exception
{
    public ArenaSetupSessionAlreadyInProgress() {}

    // Constructor that accepts a message
    public ArenaSetupSessionAlreadyInProgress(String message)
    {
        super(message);
    }
}
