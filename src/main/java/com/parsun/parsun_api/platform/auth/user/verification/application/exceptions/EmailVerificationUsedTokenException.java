package com.parsun.parsun_api.platform.auth.user.verification.application.exceptions;

public class EmailVerificationUsedTokenException extends RuntimeException {

    public EmailVerificationUsedTokenException(String message) {
        super(message);
    }
}
