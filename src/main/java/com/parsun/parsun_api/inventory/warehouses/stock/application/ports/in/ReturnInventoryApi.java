package com.parsun.parsun_api.inventory.warehouses.stock.application.ports.in;

public interface ReturnInventoryApi {
    void restoreStock(ReturnStockCommand command);
    void reverseStock(ReturnStockCommand command);
}
