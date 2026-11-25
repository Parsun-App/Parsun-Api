package com.parsun.parsun_api.inventory.warehouses.reporting.application.ports.out;

import com.parsun.parsun_api.inventory.warehouses.reporting.application.dto.response.MovementReportResponse;
import com.parsun.parsun_api.inventory.warehouses.reporting.application.dto.query.model.MovementsPdfQuery;

import java.util.List;

public interface MovementsReportRepositoryPort {
    List<MovementReportResponse> findAllForPdf(MovementsPdfQuery query);
}
