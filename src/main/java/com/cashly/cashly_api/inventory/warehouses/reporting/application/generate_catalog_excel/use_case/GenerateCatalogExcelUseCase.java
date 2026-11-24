package com.cashly.cashly_api.inventory.warehouses.reporting.application.generate_catalog_excel.use_case;

import com.cashly.cashly_api.inventory.warehouses.reporting.application.dto.query.model.CatalogPdfQuery;

public interface GenerateCatalogExcelUseCase {
    byte[] execute(CatalogPdfQuery query);
}
