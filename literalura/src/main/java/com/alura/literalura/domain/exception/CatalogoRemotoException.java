package com.alura.literalura.domain.exception;

public class CatalogoRemotoException extends RuntimeException {

    public CatalogoRemotoException(String message) {
        super(message);
    }

    public CatalogoRemotoException(String message, Throwable cause) {
        super(message, cause);
    }
}
