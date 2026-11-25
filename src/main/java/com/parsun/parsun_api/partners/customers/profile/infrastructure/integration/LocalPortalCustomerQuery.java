package com.parsun.parsun_api.partners.customers.profile.infrastructure.integration;

import com.parsun.parsun_api.partners.customers.profile.domain.entity.Customer;
import com.parsun.parsun_api.partners.customers.profile.domain.repository.CustomerRepository;
import com.parsun.parsun_api.partners.customers.profile.domain.valueobjects.CustomerId;
import com.parsun.parsun_api.partners.customers.profile.domain.valueobjects.Rif;
import com.parsun.parsun_api.platform.auth.authentication.application.ports.out.PortalCustomerQuery;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class LocalPortalCustomerQuery implements PortalCustomerQuery {

    private final CustomerRepository customers;

    public LocalPortalCustomerQuery(CustomerRepository customers) {
        this.customers = customers;
    }

    @Override
    public Optional<PortalCustomer> findByRif(String rif) {
        return customers.findActivePortalCustomerByRif(new Rif(rif))
                .map(this::toPortalCustomer);
    }

    @Override
    public Optional<PortalCustomer> findById(UUID id) {
        return customers.findById(new CustomerId(id))
                .map(this::toPortalCustomer);
    }

    @Override
    public boolean isPortalAccessEnabled(UUID id) {
        return customers.isActivePortalAccess(new CustomerId(id));
    }

    private PortalCustomer toPortalCustomer(Customer customer) {
        String email = customer.getEmails().isEmpty()
                ? null
                : customer.getEmails().get(0).getEmail().getValue();
        return new PortalCustomer(customer.getId().getValue(),
                customer.getLegalName().getValue(),
                customer.getRif().getValue(), email,
                customer.getPortalPassword(), customer.isActive(),
                customer.isPortalAccess(), customer.getCreatedAt());
    }
}
