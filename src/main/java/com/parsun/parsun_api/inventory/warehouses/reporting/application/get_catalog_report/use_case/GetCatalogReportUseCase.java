package com.parsun.parsun_api.inventory.warehouses.reporting.application.get_catalog_report.use_case;

import com.parsun.parsun_api.inventory.warehouses.reporting.application.dto.response.CatalogReportResponse;
import com.parsun.parsun_api.inventory.warehouses.reporting.application.dto.query.model.CatalogReportQuery;
import com.parsun.parsun_api.shared.dto.PageResponse;

public interface GetCatalogReportUseCase {
    PageResponse<CatalogReportResponse> execute(CatalogReportQuery query);
}
