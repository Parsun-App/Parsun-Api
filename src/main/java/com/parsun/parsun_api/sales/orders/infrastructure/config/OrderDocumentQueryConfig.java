package com.parsun.parsun_api.sales.orders.infrastructure.config;

import com.parsun.parsun_api.sales.orders.application.ports.in.OrderDocumentQuery;
import com.parsun.parsun_api.sales.orders.application.queries.services.OrderDocumentQueryService;
import com.parsun.parsun_api.sales.orders.application.list.use_case.GetOrdersUseCase;
import com.parsun.parsun_api.sales.orders.domain.repository.OrderRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderDocumentQueryConfig {
    @Bean
    public OrderDocumentQuery orderDocumentQuery(OrderRepository repository,
            GetOrdersUseCase listQuery) {
        return new OrderDocumentQueryService(repository, listQuery);
    }
}
