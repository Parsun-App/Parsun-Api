package com.parsun.parsun_api.inventory.warehouses.stock.infrastructure.config;

import com.parsun.parsun_api.inventory.warehouses.stock.application.ports.in.ProductStockQuery;
import com.parsun.parsun_api.inventory.warehouses.stock.application.queries.services.RepositoryProductStockQuery;
import com.parsun.parsun_api.inventory.warehouses.management.domain.repository.WarehouseRepository;
import com.parsun.parsun_api.inventory.warehouses.stock.domain.repository.WarehouseStockRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductStockQueryConfig {
    @Bean
    public ProductStockQuery productStockQuery(WarehouseStockRepository stocks,
            WarehouseRepository warehouses) {
        return new RepositoryProductStockQuery(stocks, warehouses);
    }
}
