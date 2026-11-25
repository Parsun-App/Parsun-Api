package com.parsun.parsun_api.sales.invoices.invoices.reporting.application.ports.out;

import com.parsun.parsun_api.sales.invoices.invoices.reporting.application.dto.model.InvoiceReportData;

public interface InvoicePdfPort {
    byte[] generatePdf(InvoiceReportData invoice);
}
