package com.cashly.cashly_api.inventory.warehouses.reporting.application.ports.out;

import com.cashly.cashly_api.inventory.warehouses.reporting.application.dto.response.CatalogExcelRow;

import java.util.List;

public interface CatalogExcelPort {
    byte[] generateExcel(List<CatalogExcelRow> rows);
}
