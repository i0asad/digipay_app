package com.asad.digipay.exception;

/**
 * Base exception for all Digipay business-logic errors.
 */
public abstract class DigipayException extends RuntimeException {
    protected DigipayException(String message) {
        super(message);
    }
}
