package com.asad.digipay.exception;

public class SameWalletTransferException extends DigipayException {
    public SameWalletTransferException(String message) {
        super(message);
    }
}
