package com.parsun.parsun_api.platform.auth.authorization.application.exceptions;

public class AccessConflictException extends RuntimeException {
    public AccessConflictException(String message) {
        super(message);
    }
}
