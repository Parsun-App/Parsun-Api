package com.parsun.parsun_api.partners.customers.credit.application.exceptions;

public class CreditLimitExceededException extends RuntimeException {
    public CreditLimitExceededException(String message) {
        super(message);
    }
}
