package com.parsun.parsun_api.inventory.warehouses.reporting.infrastructure.excel;

import com.parsun.parsun_api.inventory.warehouses.reporting.application.dto.response.CatalogExcelRow;
import com.parsun.parsun_api.inventory.warehouses.reporting.application.ports.out.CatalogExcelPort;
import com.parsun.parsun_api.shared.infrastructure.i18n.ReportMessages;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class CatalogExcelGenerator implements CatalogExcelPort {

    private static String[] headers() {
        return new String[]{
                ReportMessages.text("inventory.warehouses.reports.sku"),
                ReportMessages.text("inventory.warehouses.reports.product"),
                ReportMessages.text("inventory.warehouses.reports.category"),
                ReportMessages.text("inventory.warehouses.reports.type"),
                ReportMessages.text("inventory.warehouses.reports.status"),
                ReportMessages.text("inventory.warehouses.reports.totalStock")};
    }

    @Override
    public byte[] generateExcel(List<CatalogExcelRow> items) {
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            var sheet = workbook.createSheet(ReportMessages
                    .text("inventory.warehouses.reports.productsReport"));

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle cellStyle = createCellStyle(workbook);
            CellStyle totalStyle = createTotalStyle(workbook);

            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(ReportMessages
                    .text("inventory.warehouses.reports.productsReport"));
            titleCell.setCellStyle(createTitleStyle(workbook));
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0,
                    0, 0, headers().length - 1));

            String fecha = LocalDate.now()
                    .format(ReportMessages.dateFormatter());
            Row dateRow = sheet.createRow(1);
            Cell dateCell = dateRow.createCell(0);
            dateCell.setCellValue(ReportMessages
                    .text("inventory.warehouses.reports.generatedOn") + fecha);

            Row headerRow = sheet.createRow(3);
            for (int i = 0; i < headers().length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers()[i]);
                cell.setCellStyle(headerStyle);
            }

            long totalStock = 0L;
            for (int i = 0; i < items.size(); i++) {
                Row row = sheet.createRow(4 + i);
                CatalogExcelRow item = items.get(i);
                totalStock += item.totalStock();

                createStringCell(row, 0, item.sku(), cellStyle);
                createStringCell(row, 1, item.productName(), cellStyle);
                createStringCell(row, 2, item.categoryName(), cellStyle);
                createStringCell(row, 3, Boolean.TRUE.equals(item.composite())
                        ? ReportMessages
                                .text("inventory.warehouses.reports.composite")
                        : ReportMessages
                                .text("inventory.warehouses.reports.simple"),
                        cellStyle);
                createStringCell(row, 4, Boolean.TRUE.equals(item.active())
                        ? ReportMessages
                                .text("inventory.warehouses.reports.active")
                        : ReportMessages
                                .text("inventory.warehouses.reports.inactive"),
                        cellStyle);
                createNumericCell(row, 5, item.totalStock(), cellStyle);
            }

            Row totalRow = sheet.createRow(4 + items.size());
            createStringCell(totalRow, 0, ReportMessages
                    .text("inventory.warehouses.reports.total"), totalStyle);
            createStringCell(totalRow, 1,
                    items.size() + " " + ReportMessages
                            .text("inventory.warehouses.reports.products"),
                    totalStyle);
            for (int i = 2; i < 5; i++) {
                createStringCell(totalRow, i, "", totalStyle);
            }
            createNumericCell(totalRow, 5, totalStock, totalStyle);

            sheet.setColumnWidth(0, 4000);
            sheet.setColumnWidth(1, 8000);
            sheet.setColumnWidth(2, 6000);
            sheet.setColumnWidth(3, 4000);
            sheet.setColumnWidth(4, 4000);
            sheet.setColumnWidth(5, 4000);

            workbook.write(baos);
        } catch (IOException e) {
            throw new RuntimeException("Error generating catalog Excel", e);
        } finally {
            workbook.dispose();
        }

        return baos.toByteArray();
    }

    private CellStyle createTitleStyle(SXSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        style.setFont(font);
        return style;
    }

    private CellStyle createHeaderStyle(SXSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 10);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.BLACK.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createCellStyle(SXSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createTotalStyle(SXSSFWorkbook workbook) {
        CellStyle style = createCellStyle(workbook);
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        return style;
    }

    private void createStringCell(Row row, int col, String value,
            CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value != null ? value : "");
        cell.setCellStyle(style);
    }

    private void createNumericCell(Row row, int col, long value,
            CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }
}
