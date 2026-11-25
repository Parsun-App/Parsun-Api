package com.parsun.parsun_api.inventory.warehouses.reporting.infrastructure.web;

import com.parsun.parsun_api.inventory.warehouses.reporting.application.dto.response.CatalogReportResponse;
import com.parsun.parsun_api.inventory.warehouses.reporting.application.dto.query.model.CatalogPdfQuery;
import com.parsun.parsun_api.inventory.warehouses.reporting.application.dto.query.model.CatalogReportQuery;
import com.parsun.parsun_api.inventory.warehouses.reporting.application.generate_catalog_excel.use_case.GenerateCatalogExcelUseCase;
import com.parsun.parsun_api.inventory.warehouses.reporting.application.generate_catalog_pdf.use_case.GenerateCatalogPdfUseCase;
import com.parsun.parsun_api.inventory.warehouses.reporting.application.get_catalog_report.use_case.GetCatalogReportUseCase;
import com.parsun.parsun_api.shared.dto.PageResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/reports/inventory/catalog")
public class CatalogReportController {

    private final GetCatalogReportUseCase getCatalogReportUseCase;
    private final GenerateCatalogPdfUseCase generateCatalogPdfUseCase;
    private final GenerateCatalogExcelUseCase generateCatalogExcelUseCase;

    public CatalogReportController(
            GetCatalogReportUseCase getCatalogReportUseCase,
            GenerateCatalogPdfUseCase generateCatalogPdfUseCase,
            GenerateCatalogExcelUseCase generateCatalogExcelUseCase) {
        this.getCatalogReportUseCase = getCatalogReportUseCase;
        this.generateCatalogPdfUseCase = generateCatalogPdfUseCase;
        this.generateCatalogExcelUseCase = generateCatalogExcelUseCase;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('INVENTORY_WAREHOUSES_VIEW_STOCK')")
    public PageResponse<CatalogReportResponse> getCatalog(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID warehouseId,
            @RequestParam(required = false) Boolean composite,
            @RequestParam(required = false) Boolean withStock,
            @RequestParam(required = false) Boolean lowStock,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        CatalogReportQuery query = new CatalogReportQuery(search, categoryId,
                warehouseId, composite, withStock, lowStock, page, size);
        return getCatalogReportUseCase.execute(query);
    }

    @GetMapping("/pdf")
    @PreAuthorize("hasAuthority('INVENTORY_WAREHOUSES_REPORT_STOCK')")
    public ResponseEntity<byte[]> getCatalogPdf(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID warehouseId,
            @RequestParam(required = false) Boolean composite,
            @RequestParam(required = false) Boolean withStock,
            @RequestParam(required = false) Boolean lowStock) {
        CatalogPdfQuery query = new CatalogPdfQuery(search, categoryId,
                warehouseId, composite, withStock, lowStock);
        byte[] pdf = generateCatalogPdfUseCase.execute(query);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"reporte-productos.pdf\"")
                .body(pdf);
    }

    @GetMapping("/excel")
    @PreAuthorize("hasAuthority('INVENTORY_WAREHOUSES_REPORT_STOCK')")
    public ResponseEntity<byte[]> getCatalogExcel(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID warehouseId,
            @RequestParam(required = false) Boolean composite,
            @RequestParam(required = false) Boolean withStock,
            @RequestParam(required = false) Boolean lowStock) {
        CatalogPdfQuery query = new CatalogPdfQuery(search, categoryId,
                warehouseId, composite, withStock, lowStock);
        byte[] excel = generateCatalogExcelUseCase.execute(query);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"reporte-productos.xlsx\"")
                .body(excel);
    }
}
