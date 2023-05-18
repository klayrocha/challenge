package com.dws.challenge.exception;

/**
 * Exception class of rules in transfer
 *
 * @author klayrocha
 */
public class TransactionRuleException extends RuntimeException {

    private static final long serialVersionUID = -1584920411671514330L;

    public TransactionRuleException(String message) {
        super(message);
    }
}

