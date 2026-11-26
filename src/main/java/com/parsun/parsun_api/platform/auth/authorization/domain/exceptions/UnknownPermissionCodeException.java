package com.parsun.parsun_api.platform.auth.authorization.domain.exceptions;

public class UnknownPermissionCodeException extends RuntimeException {

    private final String permissionCode;

    public UnknownPermissionCodeException(String permissionCode) {
        super("Unknown permission code: " + permissionCode);
        this.permissionCode = permissionCode;
    }

    public String permissionCode() {
        return permissionCode;
    }
}
