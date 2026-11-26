package com.parsun.parsun_api.inventory.warehouses.reporting.application.generate_catalog_excel.services;

import com.parsun.parsun_api.inventory.warehouses.reporting.application.dto.query.model.CatalogPdfQuery;
import com.parsun.parsun_api.inventory.warehouses.reporting.application.dto.response.CatalogExcelRow;
import com.parsun.parsun_api.inventory.warehouses.reporting.application.dto.response.CatalogReportResponse;
import com.parsun.parsun_api.inventory.warehouses.reporting.application.generate_catalog_excel.use_case.GenerateCatalogExcelUseCase;
import com.parsun.parsun_api.inventory.warehouses.reporting.application.ports.out.CatalogExcelPort;
import com.parsun.parsun_api.inventory.warehouses.reporting.application.ports.out.CatalogReportRepositoryPort;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GenerateCatalogExcelUseCaseImpl
        implements GenerateCatalogExcelUseCase {

    private final CatalogReportRepositoryPort repository;
    private final CatalogExcelPort excelPort;

    public GenerateCatalogExcelUseCaseImpl(
            CatalogReportRepositoryPort repository,
            CatalogExcelPort excelPort) {
        this.repository = repository;
        this.excelPort = excelPort;
    }

    @Override
    public byte[] execute(CatalogPdfQuery query) {
        List<CatalogReportResponse> items = repository.findAllForExcel(query);
        Map<UUID, CatalogExcelRow> byProduct = new LinkedHashMap<>();
        for (CatalogReportResponse item : items) {
            BigDecimal quantity = item.quantity() != null
                    ? item.quantity()
                    : BigDecimal.ZERO;
            byProduct.merge(item.productId(),
                    new CatalogExcelRow(item.productName(), item.sku(),
                            item.categoryName(), item.composite(), quantity,
                            item.active()),
                    (current, incoming) -> new CatalogExcelRow(
                            current.productName(), current.sku(),
                            current.categoryName(), current.composite(),
                            current.totalStock().add(incoming.totalStock()),
                            current.active()));
        }
        List<CatalogExcelRow> rows = byProduct.values().stream()
                .sorted(Comparator.comparing(CatalogExcelRow::productName,
                        String.CASE_INSENSITIVE_ORDER))
                .toList();
        return excelPort.generateExcel(rows);
    }
}
