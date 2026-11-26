package com.parsun.parsun_api.sales.invoices.invoices.domain.exceptions;

import java.util.UUID;

public class InvoiceNotFoundException extends RuntimeException {

    public InvoiceNotFoundException(UUID id) {
        super("Invoice not found with id: " + id);
    }
}
