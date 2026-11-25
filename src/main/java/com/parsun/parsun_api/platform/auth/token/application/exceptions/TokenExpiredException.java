package com.parsun.parsun_api.platform.auth.token.application.exceptions;

public class TokenExpiredException extends RuntimeException {

    public TokenExpiredException(String message) {
        super(message);
    }
}
