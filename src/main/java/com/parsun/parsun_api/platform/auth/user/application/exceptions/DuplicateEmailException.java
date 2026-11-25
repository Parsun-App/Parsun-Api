package com.parsun.parsun_api.platform.auth.user.application.exceptions;

public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String message) {
        super(message);
    }
}
