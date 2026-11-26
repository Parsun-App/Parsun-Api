package com.parsun.parsun_api.inventory.warehouses.reporting.application.generate_catalog_excel.services;

import com.parsun.parsun_api.inventory.warehouses.reporting.application.dto.query.model.CatalogPdfQuery;
import com.parsun.parsun_api.inventory.warehouses.reporting.application.dto.response.CatalogExcelRow;
import com.parsun.parsun_api.inventory.warehouses.reporting.application.dto.response.CatalogReportResponse;
import com.parsun.parsun_api.inventory.warehouses.reporting.application.ports.out.CatalogExcelPort;
import com.parsun.parsun_api.inventory.warehouses.reporting.application.ports.out.CatalogReportRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GenerateCatalogExcelUseCaseImpl Unit Tests")
class GenerateCatalogExcelUseCaseImplTest {

    @Mock
    private CatalogReportRepositoryPort repository;

    @Mock
    private CatalogExcelPort excelPort;

    @InjectMocks
    private GenerateCatalogExcelUseCaseImpl useCase;

    @Test
    @DisplayName("should aggregate warehouse rows into total stock per product")
    void should_aggregate_total_stock_per_product() {
        UUID productId = UUID.randomUUID();
        CatalogPdfQuery query = new CatalogPdfQuery(null, null, null, null,
                null, null);
        List<CatalogReportResponse> items = List.of(
                new CatalogReportResponse(productId, "Product A", "SKU001",
                        UUID.randomUUID(), "Category", false, UUID.randomUUID(),
                        "Main", new BigDecimal("100"), true),
                new CatalogReportResponse(productId, "Product A", "SKU001",
                        UUID.randomUUID(), "Category", false, UUID.randomUUID(),
                        "Reserve", new BigDecimal("40"), true));
        byte[] expected = "excel".getBytes();

        when(repository.findAllForExcel(query)).thenReturn(items);
        when(excelPort.generateExcel(org.mockito.ArgumentMatchers.any()))
                .thenReturn(expected);

        byte[] result = useCase.execute(query);

        assertArrayEquals(expected, result);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<CatalogExcelRow>> captor = ArgumentCaptor
                .forClass(List.class);
        verify(excelPort).generateExcel(captor.capture());
        assertEquals(1, captor.getValue().size());
        assertEquals(0, new BigDecimal("140")
                .compareTo(captor.getValue().getFirst().totalStock()));
    }

    @Test
    @DisplayName("should generate Excel with empty result")
    void should_generate_excel_with_empty_result() {
        CatalogPdfQuery query = new CatalogPdfQuery(null, null, null, null,
                null, null);
        byte[] expected = "empty".getBytes();

        when(repository.findAllForExcel(query)).thenReturn(List.of());
        when(excelPort.generateExcel(List.of())).thenReturn(expected);

        assertArrayEquals(expected, useCase.execute(query));
    }
}
