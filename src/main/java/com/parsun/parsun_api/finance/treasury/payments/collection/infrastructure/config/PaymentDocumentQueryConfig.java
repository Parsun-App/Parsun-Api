package com.parsun.parsun_api.finance.treasury.payments.collection.infrastructure.config;
import com.parsun.parsun_api.finance.treasury.payments.collection.application.ports.in.PaymentDocumentQuery;
import com.parsun.parsun_api.finance.treasury.payments.collection.application.services.PaymentDocumentQueryService;
import com.parsun.parsun_api.finance.treasury.payments.collection.application.queries.use_case.GetPaymentByIdUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class PaymentDocumentQueryConfig {
    @Bean
    public PaymentDocumentQuery paymentDocumentQuery(
            GetPaymentByIdUseCase query) {
        return new PaymentDocumentQueryService(query);
    }
}