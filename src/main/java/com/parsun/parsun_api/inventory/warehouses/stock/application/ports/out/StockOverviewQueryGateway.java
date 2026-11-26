package com.parsun.parsun_api.inventory.warehouses.stock.application.ports.out;

import com.parsun.parsun_api.inventory.warehouses.stock.application.dto.response.StockOverviewResponse;
import com.parsun.parsun_api.inventory.warehouses.stock.application.queries.model.StockOverviewFilter;

public interface StockOverviewQueryGateway {
    StockOverviewResponse search(StockOverviewFilter filter);
}
