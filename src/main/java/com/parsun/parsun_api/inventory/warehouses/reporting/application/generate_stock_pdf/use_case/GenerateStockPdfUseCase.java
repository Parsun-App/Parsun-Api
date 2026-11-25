package com.parsun.parsun_api.inventory.warehouses.reporting.application.generate_stock_pdf.use_case;

import com.parsun.parsun_api.inventory.warehouses.reporting.application.dto.query.model.StockPdfQuery;

public interface GenerateStockPdfUseCase {
    byte[] execute(StockPdfQuery query);
}
