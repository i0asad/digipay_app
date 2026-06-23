package com.asad.digipay.exception;

public class InsufficientBalanceException extends DigipayException {
    public InsufficientBalanceException(String message) {
        super(message);
    }
}
