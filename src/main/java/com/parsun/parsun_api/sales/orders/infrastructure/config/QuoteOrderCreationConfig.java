package com.parsun.parsun_api.sales.orders.infrastructure.config;

import com.parsun.parsun_api.sales.orders.application.ports.in.QuoteOrderCreationApi;
import com.parsun.parsun_api.sales.orders.application.ports.out.OrderInventoryGateway;
import com.parsun.parsun_api.sales.orders.application.create.from_quote.services.QuoteOrderCreationService;
import com.parsun.parsun_api.sales.orders.domain.repository.OrderRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuoteOrderCreationConfig {
    @Bean
    public QuoteOrderCreationApi quoteOrderCreationApi(OrderRepository orders,
            OrderInventoryGateway inventory) {
        return new QuoteOrderCreationService(orders, inventory);
    }
}
