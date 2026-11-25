package com.parsun.parsun_api.sales.invoices.invoices.reporting.application.generate_invoices_report_pdf.use_case;

import com.parsun.parsun_api.sales.invoices.invoices.reporting.application.dto.model.InvoiceReportFilter;

public interface GenerateInvoicesReportPdfUseCase {
    byte[] execute(InvoiceReportFilter filter, String mode);
}
