package com.parsun.parsun_api.sales.quotes.reporting.application.ports.out;

import com.parsun.parsun_api.sales.quotes.reporting.application.dto.model.QuoteReportData;

public interface QuotePdfPort {
    byte[] generatePdf(QuoteReportData quote);
}
