package com.cashly.cashly_api.partners.customers.profile.application.portal.use_case;

import com.cashly.cashly_api.partners.customers.profile.application.exceptions.CustomerNotFoundException;
import com.cashly.cashly_api.partners.customers.profile.application.portal.request.ChangeCustomerPortalPasswordRequest;
import com.cashly.cashly_api.partners.customers.profile.domain.entity.Customer;
import com.cashly.cashly_api.partners.customers.profile.domain.repository.CustomerRepository;
import com.cashly.cashly_api.partners.customers.profile.domain.valueobjects.CustomerId;
import com.cashly.cashly_api.partners.customers.profile.application.ports.out.PortalPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

public class ChangeCustomerPortalPasswordUseCase {

    private final CustomerRepository customerRepository;
    private final PortalPasswordEncoder passwordEncoder;

    public ChangeCustomerPortalPasswordUseCase(
            CustomerRepository customerRepository,
            PortalPasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void execute(String customerId,
            ChangeCustomerPortalPasswordRequest request) {
        Customer customer = customerRepository
                .findById(CustomerId.from(customerId))
                .orElseThrow(() -> new CustomerNotFoundException(
                        "Customer not found with id: " + customerId));
        customer.changePortalPassword(
                passwordEncoder.encode(request.newPassword()));
        customerRepository.save(customer);
    }
}
