package com.parsun.parsun_api.sales.invoices.invoices.infrastructure.integration;

import com.parsun.parsun_api.partners.customers.credit.application.ports.in.CustomerCreditEligibilityQuery;
import com.parsun.parsun_api.sales.invoices.invoices.application.ports.out.CustomerCreditGateway;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.UUID;

@Component
public class InvoiceCustomerCreditAdapter implements CustomerCreditGateway {
    private final CustomerCreditEligibilityQuery query;

    public InvoiceCustomerCreditAdapter(CustomerCreditEligibilityQuery query) {
        this.query = query;
    }

    @Override
    public boolean isCreditAvailable(UUID customerId, BigDecimal amount) {
        return query.isCreditAvailable(customerId, amount);
    }
}
