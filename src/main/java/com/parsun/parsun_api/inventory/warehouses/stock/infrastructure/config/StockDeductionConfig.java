package com.parsun.parsun_api.inventory.warehouses.stock.infrastructure.config;

import com.parsun.parsun_api.inventory.warehouses.stock.application.ports.in.StockDeductionApi;
import com.parsun.parsun_api.inventory.warehouses.stock.application.deduction.services.StockDeductionService;
import com.parsun.parsun_api.inventory.warehouses.stock.domain.repository.WarehouseStockRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StockDeductionConfig {
    @Bean
    public StockDeductionApi stockDeductionApi(
            WarehouseStockRepository stocks) {
        return new StockDeductionService(stocks);
    }
}
