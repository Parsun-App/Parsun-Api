package com.parsun.parsun_api.inventory.warehouses.reporting.infrastructure.config;

import com.parsun.parsun_api.shared.infrastructure.pdf.PdfBranding;
import com.parsun.parsun_api.inventory.warehouses.reporting.application.ports.out.MovementsExcelPort;
import com.parsun.parsun_api.inventory.warehouses.reporting.application.ports.out.MovementsPdfPort;
import com.parsun.parsun_api.inventory.warehouses.reporting.application.ports.out.MovementsReportRepositoryPort;
import com.parsun.parsun_api.inventory.warehouses.reporting.application.generate_movements_excel.use_case.GenerateMovementsExcelUseCase;
import com.parsun.parsun_api.inventory.warehouses.reporting.application.generate_movements_excel.services.GenerateMovementsExcelUseCaseImpl;
import com.parsun.parsun_api.inventory.warehouses.reporting.application.generate_movements_pdf.use_case.GenerateMovementsPdfUseCase;
import com.parsun.parsun_api.inventory.warehouses.reporting.application.generate_movements_pdf.services.GenerateMovementsPdfUseCaseImpl;
import com.parsun.parsun_api.inventory.warehouses.reporting.infrastructure.excel.MovementsExcelGenerator;
import com.parsun.parsun_api.inventory.warehouses.reporting.infrastructure.pdf.MovementsPdfGenerator;
import com.parsun.parsun_api.inventory.warehouses.reporting.infrastructure.persistence.repositories.MovementsReportJpaRepository;
import com.parsun.parsun_api.inventory.warehouses.reporting.infrastructure.persistence.adapters.MovementsReportRepositoryAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MovementsReportConfig {

    @Bean
    public MovementsReportRepositoryPort movementsReportRepositoryPort(
            MovementsReportJpaRepository movementsReportJpaRepository) {
        return new MovementsReportRepositoryAdapter(
                movementsReportJpaRepository);
    }

    @Bean
    public MovementsPdfPort movementsPdfPort(PdfBranding branding) {
        return new MovementsPdfGenerator(branding);
    }

    @Bean
    public MovementsExcelPort movementsExcelPort() {
        return new MovementsExcelGenerator();
    }

    @Bean
    public GenerateMovementsPdfUseCase generateMovementsPdfUseCase(
            MovementsReportRepositoryPort movementsReportRepositoryPort,
            MovementsPdfPort movementsPdfPort) {
        return new GenerateMovementsPdfUseCaseImpl(
                movementsReportRepositoryPort, movementsPdfPort);
    }

    @Bean
    public GenerateMovementsExcelUseCase generateMovementsExcelUseCase(
            MovementsReportRepositoryPort movementsReportRepositoryPort,
            MovementsExcelPort movementsExcelPort) {
        return new GenerateMovementsExcelUseCaseImpl(
                movementsReportRepositoryPort, movementsExcelPort);
    }
}
