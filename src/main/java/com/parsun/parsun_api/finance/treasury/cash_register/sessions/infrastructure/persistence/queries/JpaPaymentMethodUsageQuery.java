package com.parsun.parsun_api.finance.treasury.cash_register.sessions.infrastructure.persistence.queries;

import com.parsun.parsun_api.finance.treasury.cash_register.sessions.application.ports.in.QueryPaymentMethodUsage;
import com.parsun.parsun_api.finance.treasury.cash_register.sessions.infrastructure.persistence.repositories.CashRegisterJpaRepository;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component("cashregisterPaymentMethodUsageQuery")
public class JpaPaymentMethodUsageQuery implements QueryPaymentMethodUsage {
    private final CashRegisterJpaRepository repository;

    public JpaPaymentMethodUsageQuery(CashRegisterJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean hasUsage(UUID paymentMethodId) {
        return repository.countDetailsByPaymentMethodId(paymentMethodId) > 0;
    }
}
