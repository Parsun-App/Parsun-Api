package com.parsun.parsun_api.sales.invoices.invoices.infrastructure.config;

import com.parsun.parsun_api.sales.invoices.invoices.application.ports.in.QuoteInvoiceCreationApi;
import com.parsun.parsun_api.sales.invoices.invoices.application.ports.out.ActorIdentityGateway;
import com.parsun.parsun_api.sales.invoices.invoices.application.ports.out.CustomerCreditTermsGateway;
import com.parsun.parsun_api.sales.invoices.invoices.application.issue.services.IssueInvoiceService;
import com.parsun.parsun_api.sales.invoices.invoices.application.create.from_quote.services.QuoteInvoiceCreationService;
import com.parsun.parsun_api.sales.invoices.invoices.domain.repository.InvoiceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuoteInvoiceCreationConfig {
    @Bean
    public QuoteInvoiceCreationApi quoteInvoiceCreationApi(
            InvoiceRepository invoices, ActorIdentityGateway actors,
            CustomerCreditTermsGateway creditTerms,
            IssueInvoiceService issuance) {
        return new QuoteInvoiceCreationService(invoices, actors, creditTerms,
                issuance);
    }
}
