package com.parsun.parsun_api.sales.invoices.invoices.reporting.application.ports.out;

import com.parsun.parsun_api.sales.invoices.invoices.reporting.application.dto.model.InvoiceReportRow;

import java.util.List;

public interface InvoicesReportPdfPort {
    byte[] generatePdf(List<InvoiceReportRow> invoices, String mode);
}
