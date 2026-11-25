package com.parsun.parsun_api.inventory.warehouses.stock.application.ports.out;

import com.parsun.parsun_api.inventory.warehouses.stock.application.movements.model.StockMovementFilter;
import com.parsun.parsun_api.inventory.warehouses.stock.application.movements.response.StockMovementsResponse;

public interface StockMovementQueryGateway {
    StockMovementsResponse search(StockMovementFilter filter);
}
