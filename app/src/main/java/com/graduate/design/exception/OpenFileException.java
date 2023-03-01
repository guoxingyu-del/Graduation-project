package com.graduate.design.exception;

public class OpenFileException extends RuntimeException {
    public OpenFileException() {
    }

    public OpenFileException(String message) {
        super(message);
    }
}
