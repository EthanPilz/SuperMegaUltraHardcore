package com.ethanpilz.smuhc.exceptions;

public class SaveToDatabaseException extends Exception {

    public SaveToDatabaseException() {}

    // Constructor that accepts a message
    public SaveToDatabaseException(String message) {
        super(message);
    }
}
