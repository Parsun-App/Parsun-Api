package com.parsun.parsun_api.inventory.warehouses.reporting.application.ports.out;

import com.parsun.parsun_api.inventory.warehouses.reporting.application.dto.response.StockReportResponse;
import com.parsun.parsun_api.inventory.warehouses.reporting.application.dto.query.model.StockPdfQuery;

import java.util.List;

public interface StockReportRepositoryPort {
    List<StockReportResponse> findAllForPdf(StockPdfQuery query);
}
