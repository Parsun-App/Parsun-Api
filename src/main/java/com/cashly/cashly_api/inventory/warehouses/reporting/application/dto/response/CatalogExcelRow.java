package com.cashly.cashly_api.inventory.warehouses.reporting.application.dto.response;

public record CatalogExcelRow(String productName, String sku,
        String categoryName, Boolean composite, long totalStock,
        Boolean active) {
}
