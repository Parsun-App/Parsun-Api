package com.parsun.parsun_api.inventory.warehouses.reporting.application.ports.out;

import com.parsun.parsun_api.inventory.warehouses.reporting.application.dto.response.MovementReportResponse;

import java.util.List;

public interface MovementsPdfPort {
    byte[] generatePdf(List<MovementReportResponse> items);
}
