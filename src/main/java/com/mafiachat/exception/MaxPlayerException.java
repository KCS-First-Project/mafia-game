package com.mafiachat.exception;

public class MaxPlayerException extends RuntimeException {
    public MaxPlayerException() {
    }

    public MaxPlayerException(String message) {
        super(message);
    }

}
