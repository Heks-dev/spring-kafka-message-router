package com.hal.kafka;

public class IncompatibleTypeException extends RuntimeException {

    public IncompatibleTypeException() {
    }

    public IncompatibleTypeException(String message) {
        super(message);
    }

    public IncompatibleTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncompatibleTypeException(Throwable cause) {
        super(cause);
    }
}
