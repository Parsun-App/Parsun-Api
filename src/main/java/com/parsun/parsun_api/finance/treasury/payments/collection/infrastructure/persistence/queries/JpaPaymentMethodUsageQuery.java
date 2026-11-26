package com.parsun.parsun_api.finance.treasury.payments.collection.infrastructure.persistence.queries;

import com.parsun.parsun_api.finance.treasury.payments.collection.application.ports.in.QueryPaymentMethodUsage;
import com.parsun.parsun_api.finance.treasury.payments.collection.infrastructure.persistence.repositories.PaymentJpaRepository;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component("paymentsPaymentMethodUsageQuery")
public class JpaPaymentMethodUsageQuery implements QueryPaymentMethodUsage {
    private final PaymentJpaRepository repository;

    public JpaPaymentMethodUsageQuery(PaymentJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean hasUsage(UUID paymentMethodId) {
        return repository.countByPaymentMethodId(paymentMethodId) > 0;
    }
}
