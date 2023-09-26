package com.konopleva.crudeapp.exception;

public class IdempotenceException extends RuntimeException{
    public IdempotenceException(String message) {
        super(message);
    }
}
