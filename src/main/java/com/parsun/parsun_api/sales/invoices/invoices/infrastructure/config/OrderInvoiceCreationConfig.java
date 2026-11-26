package com.parsun.parsun_api.sales.invoices.invoices.infrastructure.config;

import com.parsun.parsun_api.sales.invoices.invoices.application.ports.in.OrderInvoiceCreationApi;
import com.parsun.parsun_api.sales.invoices.invoices.application.create.from_order.services.OrderInvoiceCreationService;
import com.parsun.parsun_api.sales.invoices.invoices.application.create.use_case.CreateInvoiceUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderInvoiceCreationConfig {
    @Bean
    public OrderInvoiceCreationApi orderInvoiceCreationApi(
            CreateInvoiceUseCase createInvoice) {
        return new OrderInvoiceCreationService(createInvoice);
    }
}
