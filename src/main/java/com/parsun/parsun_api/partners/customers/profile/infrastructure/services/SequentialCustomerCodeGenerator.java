package com.parsun.parsun_api.partners.customers.profile.infrastructure.services;

import com.parsun.parsun_api.partners.customers.profile.domain.repository.CustomerRepository;
import com.parsun.parsun_api.partners.customers.profile.domain.services.CustomerCodeGenerator;
import com.parsun.parsun_api.partners.customers.profile.domain.valueobjects.CustomerCode;

public class SequentialCustomerCodeGenerator implements CustomerCodeGenerator {

    private final CustomerRepository customerRepository;

    public SequentialCustomerCodeGenerator(
            CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public CustomerCode generateNext() {
        long nextCode = customerRepository.getNextCode();
        return new CustomerCode(nextCode);
    }
}
