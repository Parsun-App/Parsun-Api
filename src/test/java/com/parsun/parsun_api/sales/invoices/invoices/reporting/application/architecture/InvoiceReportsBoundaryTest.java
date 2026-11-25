package com.parsun.parsun_api.sales.invoices.invoices.reporting.application.architecture;

import com.parsun.parsun_api.sales.invoices.invoices.reporting.application.ports.out.InvoicePdfPort;
import com.parsun.parsun_api.sales.invoices.invoices.reporting.application.ports.out.InvoicesReportPdfPort;
import com.parsun.parsun_api.sales.invoices.invoices.reporting.application.generate_invoice_pdf.services.GenerateInvoicePdfUseCaseImpl;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.assertFalse;

class InvoiceReportsBoundaryTest {
    @Test
    void applicationContractsDoNotExposeInvoiceModuleDtosOrConcreteUseCases() {
        for (Class<?> type : new Class<?>[]{InvoicePdfPort.class,
                InvoicesReportPdfPort.class,
                GenerateInvoicePdfUseCaseImpl.class}) {
            Arrays.stream(type.getDeclaredMethods()).forEach(method -> Arrays
                    .stream(method.getGenericParameterTypes())
                    .forEach(parameter -> assertFalse(
                            parameter.getTypeName().matches(
                                    ".*parsun_api.sales.invoices.invoices.(?!reporting\\.).*"),
                            parameter.getTypeName())));
            Arrays.stream(type.getDeclaredFields())
                    .forEach(field -> assertFalse(
                            field.getGenericType().getTypeName().matches(
                                    ".*parsun_api.sales.invoices.invoices.(?!reporting\\.).*"),
                            field.toString()));
        }
    }
}
