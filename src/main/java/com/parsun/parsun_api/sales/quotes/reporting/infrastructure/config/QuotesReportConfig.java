package com.parsun.parsun_api.sales.quotes.reporting.infrastructure.config;
import com.parsun.parsun_api.shared.infrastructure.pdf.PdfBranding;
import com.parsun.parsun_api.sales.quotes.reporting.application.ports.out.QuoteReportQueryGateway;
import com.parsun.parsun_api.sales.quotes.reporting.application.ports.out.QuotePdfPort;
import com.parsun.parsun_api.sales.quotes.reporting.application.ports.out.QuotesReportPdfPort;
import com.parsun.parsun_api.sales.quotes.reporting.application.generate_quote_pdf.use_case.GenerateQuotePdfUseCase;
import com.parsun.parsun_api.sales.quotes.reporting.application.generate_quote_pdf.services.GenerateQuotePdfUseCaseImpl;
import com.parsun.parsun_api.sales.quotes.reporting.application.generate_quotes_report_pdf.use_case.GenerateQuotesReportPdfUseCase;
import com.parsun.parsun_api.sales.quotes.reporting.application.generate_quotes_report_pdf.services.GenerateQuotesReportPdfUseCaseImpl;
import com.parsun.parsun_api.sales.quotes.reporting.infrastructure.pdf.QuotePdfGenerator;
import com.parsun.parsun_api.sales.quotes.reporting.infrastructure.pdf.QuotesReportPdfGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class QuotesReportConfig {
    @Bean
    public QuotePdfPort quotePdfPort(PdfBranding branding) {
        return new QuotePdfGenerator(branding);
    }
    @Bean
    public QuotesReportPdfPort quotesReportPdfPort(PdfBranding branding) {
        return new QuotesReportPdfGenerator(branding);
    }
    @Bean
    public GenerateQuotePdfUseCase generateQuotePdfUseCase(
            QuoteReportQueryGateway query, QuotePdfPort pdf) {
        return new GenerateQuotePdfUseCaseImpl(query, pdf);
    }
    @Bean
    public GenerateQuotesReportPdfUseCase generateQuotesReportPdfUseCase(
            QuoteReportQueryGateway query, QuotesReportPdfPort pdf) {
        return new GenerateQuotesReportPdfUseCaseImpl(query, pdf);
    }
}