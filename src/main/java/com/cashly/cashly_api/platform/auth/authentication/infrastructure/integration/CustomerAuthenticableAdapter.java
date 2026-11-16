package com.cashly.cashly_api.platform.auth.authentication.infrastructure.integration;

import com.cashly.cashly_api.platform.auth.authentication.application.ports.out.PortalCustomerQuery;
import com.cashly.cashly_api.platform.auth.authentication.domain.entity.Authenticable;
import com.cashly.cashly_api.platform.auth.authentication.domain.valueobjects.AuthenticableType;

import java.time.LocalDateTime;

public class CustomerAuthenticableAdapter implements Authenticable {

    private final PortalCustomerQuery.PortalCustomer customer;

    public CustomerAuthenticableAdapter(
            PortalCustomerQuery.PortalCustomer customer) {
        this.customer = customer;
    }

    @Override
    public String getSubjectId() {
        return customer.id().toString();
    }

    @Override
    public String getEmail() {
        return customer.email() == null ? "" : customer.email();
    }

    @Override
    public String getHashedPassword() {
        return customer.hashedPassword();
    }

    @Override
    public String getUserName() {
        return customer.legalName();
    }

    @Override
    public AuthenticableType getType() {
        return AuthenticableType.CUSTOMER;
    }

    @Override
    public boolean canAuthenticate() {
        return customer.active() && customer.portalAccess()
                && customer.hashedPassword() != null
                && !customer.hashedPassword().isBlank();
    }

    @Override
    public boolean isActive() {
        return customer.active();
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return customer.createdAt();
    }
}
