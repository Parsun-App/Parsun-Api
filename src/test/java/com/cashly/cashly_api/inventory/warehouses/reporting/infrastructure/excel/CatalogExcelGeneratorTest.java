package com.cashly.cashly_api.inventory.warehouses.reporting.infrastructure.excel;

import com.cashly.cashly_api.inventory.warehouses.reporting.application.dto.response.CatalogExcelRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CatalogExcelGeneratorTest {

    @Test
    void should_generate_excel_with_total_stock_per_product() throws Exception {
        byte[] bytes = new CatalogExcelGenerator().generateExcel(List.of(
                new CatalogExcelRow("Product A", "SKU001", "Category", false,
                        140L, true),
                new CatalogExcelRow("Product B", "SKU002", "Category", false,
                        60L, true)));

        try (var workbook = new XSSFWorkbook(
                new ByteArrayInputStream(bytes))) {
            var sheet = workbook.getSheetAt(0);
            assertEquals("Product A",
                    sheet.getRow(4).getCell(1).getStringCellValue());
            assertEquals(140.0,
                    sheet.getRow(4).getCell(5).getNumericCellValue());
            assertEquals("Total",
                    sheet.getRow(6).getCell(0).getStringCellValue());
            assertEquals(200.0,
                    sheet.getRow(6).getCell(5).getNumericCellValue());
        }
    }
}
