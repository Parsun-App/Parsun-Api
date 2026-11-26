package com.parsun.parsun_api.partners.customers.profile.infrastructure.config;
import com.parsun.parsun_api.partners.customers.profile.application.ports.in.InvoiceCustomerQuery;
import com.parsun.parsun_api.partners.customers.profile.application.queries.services.RepositoryInvoiceCustomerQuery;
import com.parsun.parsun_api.partners.customers.profile.domain.repository.CustomerRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class InvoiceCustomerQueryConfig {
    @Bean
    public InvoiceCustomerQuery invoiceCustomerQuery(
            CustomerRepository customers) {
        return new RepositoryInvoiceCustomerQuery(customers);
    }
}