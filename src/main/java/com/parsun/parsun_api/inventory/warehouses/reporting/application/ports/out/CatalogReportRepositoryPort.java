package com.parsun.parsun_api.inventory.warehouses.reporting.application.ports.out;

import com.parsun.parsun_api.inventory.warehouses.reporting.application.dto.query.model.CatalogPdfQuery;
import com.parsun.parsun_api.inventory.warehouses.reporting.application.dto.response.CatalogReportResponse;
import com.parsun.parsun_api.inventory.warehouses.reporting.application.dto.query.model.CatalogReportQuery;
import com.parsun.parsun_api.shared.dto.PageResponse;

import java.util.List;

public interface CatalogReportRepositoryPort {
    PageResponse<CatalogReportResponse> findCatalog(CatalogReportQuery query);

    List<CatalogReportResponse> findAllForExcel(CatalogPdfQuery query);
}
