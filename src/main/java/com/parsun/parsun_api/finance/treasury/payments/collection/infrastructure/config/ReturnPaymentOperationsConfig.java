package com.parsun.parsun_api.finance.treasury.payments.collection.infrastructure.config;
import com.parsun.parsun_api.finance.treasury.payments.collection.application.ports.in.ReturnPaymentOperations;
import com.parsun.parsun_api.finance.treasury.payments.collection.application.services.ReturnPaymentOperationsService;
import com.parsun.parsun_api.finance.treasury.payments.collection.domain.repository.PaymentRepository;
import com.parsun.parsun_api.finance.treasury.payments.collection.domain.repository.PaymentAllocationRepository;
import com.parsun.parsun_api.finance.treasury.payments.collection.domain.services.PaymentAllocationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class ReturnPaymentOperationsConfig {
    @Bean
    public ReturnPaymentOperations returnPaymentOperations(
            PaymentRepository payments, PaymentAllocationRepository allocations,
            PaymentAllocationService service) {
        return new ReturnPaymentOperationsService(payments, allocations,
                service);
    }
}
