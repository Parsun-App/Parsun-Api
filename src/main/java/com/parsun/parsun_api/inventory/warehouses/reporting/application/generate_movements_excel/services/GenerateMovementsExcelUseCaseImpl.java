package com.parsun.parsun_api.inventory.warehouses.reporting.application.generate_movements_excel.services;

import com.parsun.parsun_api.inventory.warehouses.reporting.application.generate_movements_excel.use_case.GenerateMovementsExcelUseCase;

import com.parsun.parsun_api.inventory.warehouses.reporting.application.dto.response.MovementReportResponse;
import com.parsun.parsun_api.inventory.warehouses.reporting.application.ports.out.MovementsExcelPort;
import com.parsun.parsun_api.inventory.warehouses.reporting.application.ports.out.MovementsReportRepositoryPort;
import com.parsun.parsun_api.inventory.warehouses.reporting.application.dto.query.model.MovementsPdfQuery;

import java.util.List;

public class GenerateMovementsExcelUseCaseImpl
        implements
            GenerateMovementsExcelUseCase {

    private final MovementsReportRepositoryPort repository;
    private final MovementsExcelPort excelPort;

    public GenerateMovementsExcelUseCaseImpl(
            MovementsReportRepositoryPort repository,
            MovementsExcelPort excelPort) {
        this.repository = repository;
        this.excelPort = excelPort;
    }

    @Override
    public byte[] execute(MovementsPdfQuery query) {
        List<MovementReportResponse> items = repository.findAllForPdf(query);
        return excelPort.generateExcel(items);
    }
}
