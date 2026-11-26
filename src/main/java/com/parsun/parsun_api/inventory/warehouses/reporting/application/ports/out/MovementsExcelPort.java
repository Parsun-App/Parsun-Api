package com.parsun.parsun_api.inventory.warehouses.reporting.application.ports.out;

import com.parsun.parsun_api.inventory.warehouses.reporting.application.dto.response.MovementReportResponse;

import java.util.List;

public interface MovementsExcelPort {
    byte[] generateExcel(List<MovementReportResponse> items);
}
