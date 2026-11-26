package com.parsun.parsun_api.partners.suppliers.application.exceptions;

public class SupplierNotFoundException extends RuntimeException {
    public SupplierNotFoundException(String message) {
        super(message);
    }
}
