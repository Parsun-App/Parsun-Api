package com.parsun.parsun_api.sales.quotes.application.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record WarehouseStockInfoResponse(UUID warehouseId, String warehouseName,
        BigDecimal available) {
}
