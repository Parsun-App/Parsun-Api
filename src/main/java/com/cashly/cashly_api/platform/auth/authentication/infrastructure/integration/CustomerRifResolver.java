package com.cashly.cashly_api.platform.auth.authentication.infrastructure.integration;

import com.cashly.cashly_api.platform.auth.authentication.application.ports.out.PortalCustomerQuery;
import com.cashly.cashly_api.platform.auth.authentication.domain.entity.Authenticable;
import com.cashly.cashly_api.platform.auth.authentication.domain.services.AuthenticableResolver;

import java.util.Optional;

public class CustomerRifResolver implements AuthenticableResolver {

    private final PortalCustomerQuery customers;

    public CustomerRifResolver(PortalCustomerQuery customers) {
        this.customers = customers;
    }

    @Override
    public Optional<Authenticable> resolveByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public Optional<Authenticable> resolveByRif(String rif) {
        if (rif == null || rif.isBlank()) {
            return Optional.empty();
        }
        return customers.findByRif(rif.trim())
                .map(CustomerAuthenticableAdapter::new);
    }
}
