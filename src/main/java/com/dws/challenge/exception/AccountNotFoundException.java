package com.dws.challenge.exception;

/**
 * Exception class for Account not found
 *
 * @author klayrocha
 */
public class AccountNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 5134622683496897789L;

    public AccountNotFoundException(String message) {
        super(message);
    }
}
