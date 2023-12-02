package com.konopleva.crudeapp.exception;

import org.springframework.core.NestedRuntimeException;

public class NoEnoughMoneyException extends NestedRuntimeException {
    public NoEnoughMoneyException(String msg) {
        super(msg);
    }
}
