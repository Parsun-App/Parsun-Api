package com.parsun.parsun_api.partners.customers.warehouse_access.infrastructure.config;

import com.parsun.parsun_api.partners.customers.warehouse_access.application.ports.in.CustomerWarehouseAccessQuery;
import com.parsun.parsun_api.partners.customers.warehouse_access.application.queries.services.RepositoryCustomerWarehouseAccessQuery;
import com.parsun.parsun_api.partners.customers.warehouse_access.domain.repository.CustomerWarehouseAccessRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomerWarehouseAccessQueryConfig {
    @Bean
    public CustomerWarehouseAccessQuery customerWarehouseAccessQuery(
            CustomerWarehouseAccessRepository repository) {
        return new RepositoryCustomerWarehouseAccessQuery(repository);
    }
}
