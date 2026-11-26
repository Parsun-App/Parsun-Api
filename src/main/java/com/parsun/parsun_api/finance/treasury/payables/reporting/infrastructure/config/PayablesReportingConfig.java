package com.parsun.parsun_api.finance.treasury.payables.reporting.infrastructure.config;

import com.parsun.parsun_api.finance.treasury.payables.application.ports.out.PayablesQueryGateway;
import com.parsun.parsun_api.finance.treasury.payables.reporting.application.generate_accounts_payable_pdf.use_case.GenerateAccountsPayablePdfUseCase;
import com.parsun.parsun_api.finance.treasury.payables.reporting.application.ports.out.AccountsPayableReportPdfPort;
import com.parsun.parsun_api.finance.treasury.payables.reporting.infrastructure.pdf.AccountsPayablePdfGenerator;
import com.parsun.parsun_api.shared.infrastructure.pdf.PdfBranding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PayablesReportingConfig {

    @Bean
    public AccountsPayablePdfGenerator accountsPayablePdfGenerator(
            PdfBranding branding, PayablesQueryGateway gateway) {
        return new AccountsPayablePdfGenerator(branding, gateway);
    }

    @Bean
    public GenerateAccountsPayablePdfUseCase generateAccountsPayablePdfUseCase(
            AccountsPayableReportPdfPort pdfPort) {
        return new GenerateAccountsPayablePdfUseCase(pdfPort);
    }
}
