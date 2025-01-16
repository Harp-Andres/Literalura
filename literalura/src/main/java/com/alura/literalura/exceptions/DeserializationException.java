package com.alura.literalura.exceptions;

public class DeserializationException extends RuntimeException {
    public DeserializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
