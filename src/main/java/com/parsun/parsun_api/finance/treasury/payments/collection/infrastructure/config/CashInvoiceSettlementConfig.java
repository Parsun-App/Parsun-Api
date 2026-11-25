package com.parsun.parsun_api.finance.treasury.payments.collection.infrastructure.config;
import com.parsun.parsun_api.finance.treasury.payments.collection.application.ports.in.CashInvoiceSettlement;
import com.parsun.parsun_api.finance.treasury.payments.collection.application.services.CashInvoiceSettlementService;
import com.parsun.parsun_api.finance.treasury.payments.collection.application.ports.out.CashRegisterGateway;
import com.parsun.parsun_api.finance.treasury.payments.collection.domain.repository.PaymentRepository;
import com.parsun.parsun_api.finance.treasury.payments.collection.domain.repository.PaymentAllocationRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class CashInvoiceSettlementConfig {
    @Bean
    public CashInvoiceSettlement cashInvoiceSettlement(
            PaymentRepository payments, PaymentAllocationRepository allocations,
            CashRegisterGateway cashRegister) {
        return new CashInvoiceSettlementService(payments, allocations,
                cashRegister);
    }
}
