package com.parsun.parsun_api.inventory.warehouses.reporting.application.dto.response;

import java.math.BigDecimal;

public record CatalogExcelRow(String productName, String sku,
        String categoryName, Boolean composite, BigDecimal totalStock,
        Boolean active) {
}
