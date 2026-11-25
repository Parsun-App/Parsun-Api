package com.parsun.parsun_api.sales.quotes.reporting.application.ports.out;

import com.parsun.parsun_api.sales.quotes.reporting.application.dto.model.QuoteReportRow;

import java.util.List;

public interface QuotesReportPdfPort {
    byte[] generatePdf(List<QuoteReportRow> quotes, String mode);
}
