package com.parsun.parsun_api.partners.customers.profile.infrastructure.config;

import com.parsun.parsun_api.partners.customers.profile.application.ports.in.CustomerProfileQuery;
import com.parsun.parsun_api.partners.customers.profile.application.queries.services.RepositoryCustomerProfileQuery;
import com.parsun.parsun_api.partners.customers.balance.domain.repository.CustomerBalanceRepository;
import com.parsun.parsun_api.partners.customers.profile.domain.repository.CustomerRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomerProfileQueryConfig {
    @Bean
    public CustomerProfileQuery customerProfileQuery(
            CustomerRepository customers, CustomerBalanceRepository balances) {
        return new RepositoryCustomerProfileQuery(customers, balances);
    }
}
