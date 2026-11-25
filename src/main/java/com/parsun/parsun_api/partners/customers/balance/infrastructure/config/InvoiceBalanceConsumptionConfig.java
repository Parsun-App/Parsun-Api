package com.parsun.parsun_api.partners.customers.balance.infrastructure.config;
import com.parsun.parsun_api.partners.customers.balance.application.ports.in.InvoiceBalanceConsumption;
import com.parsun.parsun_api.partners.customers.balance.application.services.InvoiceBalanceConsumptionService;
import com.parsun.parsun_api.partners.customers.balance.application.consume.use_case.ConsumeCustomerBalanceUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class InvoiceBalanceConsumptionConfig {
    @Bean
    public InvoiceBalanceConsumption invoiceBalanceConsumption(
            ConsumeCustomerBalanceUseCase consumeBalance) {
        return new InvoiceBalanceConsumptionService(consumeBalance);
    }
}
