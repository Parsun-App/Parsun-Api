package com.parsun.parsun_api.sales.invoices.invoices.domain.exceptions;

import com.parsun.parsun_api.sales.invoices.invoices.domain.valueobjects.InvoiceId;
import com.parsun.parsun_api.sales.invoices.invoices.domain.valueobjects.InvoiceStatus;

public class InvoiceNotReturnableException extends RuntimeException {

    public InvoiceNotReturnableException(InvoiceId id,
            InvoiceStatus currentStatus) {
        super("La factura " + id.value()
                + " no acepta devoluciones. Estado actual: " + currentStatus);
    }
}
