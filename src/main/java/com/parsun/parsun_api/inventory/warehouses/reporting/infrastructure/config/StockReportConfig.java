package com.parsun.parsun_api.inventory.warehouses.reporting.infrastructure.config;

import com.parsun.parsun_api.shared.infrastructure.pdf.PdfBranding;
import com.parsun.parsun_api.inventory.warehouses.reporting.application.ports.out.StockExcelPort;
import com.parsun.parsun_api.inventory.warehouses.reporting.application.ports.out.StockPdfPort;
import com.parsun.parsun_api.inventory.warehouses.reporting.application.ports.out.StockReportRepositoryPort;
import com.parsun.parsun_api.inventory.warehouses.reporting.application.generate_stock_excel.use_case.GenerateStockExcelUseCase;
import com.parsun.parsun_api.inventory.warehouses.reporting.application.generate_stock_excel.services.GenerateStockExcelUseCaseImpl;
import com.parsun.parsun_api.inventory.warehouses.reporting.application.generate_stock_pdf.use_case.GenerateStockPdfUseCase;
import com.parsun.parsun_api.inventory.warehouses.reporting.application.generate_stock_pdf.services.GenerateStockPdfUseCaseImpl;
import com.parsun.parsun_api.inventory.warehouses.reporting.infrastructure.excel.StockExcelGenerator;
import com.parsun.parsun_api.inventory.warehouses.reporting.infrastructure.pdf.StockPdfGenerator;
import com.parsun.parsun_api.inventory.warehouses.reporting.infrastructure.persistence.repositories.StockReportJpaRepository;
import com.parsun.parsun_api.inventory.warehouses.reporting.infrastructure.persistence.adapters.StockReportRepositoryAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StockReportConfig {

    @Bean
    public StockReportRepositoryPort stockReportRepositoryPort(
            StockReportJpaRepository stockReportJpaRepository) {
        return new StockReportRepositoryAdapter(stockReportJpaRepository);
    }

    @Bean
    public StockPdfPort stockPdfPort(PdfBranding branding) {
        return new StockPdfGenerator(branding);
    }

    @Bean
    public StockExcelPort stockExcelPort() {
        return new StockExcelGenerator();
    }

    @Bean
    public GenerateStockPdfUseCase generateStockPdfUseCase(
            StockReportRepositoryPort stockReportRepositoryPort,
            StockPdfPort stockPdfPort) {
        return new GenerateStockPdfUseCaseImpl(stockReportRepositoryPort,
                stockPdfPort);
    }

    @Bean
    public GenerateStockExcelUseCase generateStockExcelUseCase(
            StockReportRepositoryPort stockReportRepositoryPort,
            StockExcelPort stockExcelPort) {
        return new GenerateStockExcelUseCaseImpl(stockReportRepositoryPort,
                stockExcelPort);
    }
}
