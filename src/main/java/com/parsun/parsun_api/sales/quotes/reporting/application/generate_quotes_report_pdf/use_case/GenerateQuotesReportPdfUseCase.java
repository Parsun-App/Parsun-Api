package com.parsun.parsun_api.sales.quotes.reporting.application.generate_quotes_report_pdf.use_case;

import com.parsun.parsun_api.sales.quotes.reporting.application.dto.model.QuoteReportFilter;

public interface GenerateQuotesReportPdfUseCase {
    byte[] execute(QuoteReportFilter filter, String mode);
}
