package com.parsun.parsun_api.sales.invoices.invoices.reporting.infrastructure.config;

import com.parsun.parsun_api.shared.infrastructure.pdf.PdfBranding;
import com.parsun.parsun_api.sales.invoices.invoices.reporting.application.ports.out.InvoicePdfPort;
import com.parsun.parsun_api.sales.invoices.invoices.reporting.application.ports.out.InvoicesReportPdfPort;
import com.parsun.parsun_api.sales.invoices.invoices.reporting.application.generate_invoice_pdf.use_case.GenerateInvoicePdfUseCase;
import com.parsun.parsun_api.sales.invoices.invoices.reporting.application.generate_invoice_pdf.services.GenerateInvoicePdfUseCaseImpl;
import com.parsun.parsun_api.sales.invoices.invoices.reporting.application.generate_invoices_report_pdf.use_case.GenerateInvoicesReportPdfUseCase;
import com.parsun.parsun_api.sales.invoices.invoices.reporting.application.generate_invoices_report_pdf.services.GenerateInvoicesReportPdfUseCaseImpl;
import com.parsun.parsun_api.sales.invoices.invoices.reporting.infrastructure.pdf.InvoicePdfGenerator;
import com.parsun.parsun_api.sales.invoices.invoices.reporting.infrastructure.pdf.InvoicesReportPdfGenerator;
import com.parsun.parsun_api.sales.invoices.invoices.reporting.application.ports.out.InvoiceReportQueryGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InvoicesReportConfig {

    @Bean
    public InvoicePdfPort invoicePdfPort(PdfBranding branding) {
        return new InvoicePdfGenerator(branding);
    }

    @Bean
    public InvoicesReportPdfPort invoicesReportPdfPort(PdfBranding branding) {
        return new InvoicesReportPdfGenerator(branding);
    }

    @Bean
    public GenerateInvoicePdfUseCase generateInvoicePdfUseCase(
            InvoiceReportQueryGateway query, InvoicePdfPort invoicePdfPort) {
        return new GenerateInvoicePdfUseCaseImpl(query, invoicePdfPort);
    }

    @Bean
    public GenerateInvoicesReportPdfUseCase generateInvoicesReportPdfUseCase(
            InvoiceReportQueryGateway query,
            InvoicesReportPdfPort invoicesReportPdfPort) {
        return new GenerateInvoicesReportPdfUseCaseImpl(query,
                invoicesReportPdfPort);
    }
}
