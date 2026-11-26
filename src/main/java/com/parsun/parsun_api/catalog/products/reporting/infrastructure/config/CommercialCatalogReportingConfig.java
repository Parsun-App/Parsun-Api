package com.parsun.parsun_api.catalog.products.reporting.infrastructure.config;

import com.parsun.parsun_api.shared.infrastructure.pdf.PdfBranding;
import com.parsun.parsun_api.catalog.products.reporting.application.ports.out.CommercialCatalogPdfPort;
import com.parsun.parsun_api.catalog.products.reporting.application.ports.out.CommercialCatalogRepositoryPort;
import com.parsun.parsun_api.catalog.products.reporting.application.generate_commercial_catalog_pdf.use_case.GenerateCommercialCatalogPdfUseCase;
import com.parsun.parsun_api.catalog.products.reporting.application.generate_commercial_catalog_pdf.services.GenerateCommercialCatalogPdfUseCaseImpl;
import com.parsun.parsun_api.catalog.products.reporting.infrastructure.pdf.CommercialCatalogPdfGenerator;
import com.parsun.parsun_api.catalog.products.reporting.infrastructure.persistence.repositories.CommercialCatalogJpaRepository;
import com.parsun.parsun_api.catalog.products.reporting.infrastructure.persistence.adapters.CommercialCatalogRepositoryAdapter;
import com.parsun.parsun_api.catalog.products.reporting.application.ports.out.MediaGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommercialCatalogReportingConfig {

    @Bean
    public CommercialCatalogRepositoryPort commercialCatalogRepositoryPort(
            CommercialCatalogJpaRepository commercialCatalogJpaRepository) {
        return new CommercialCatalogRepositoryAdapter(
                commercialCatalogJpaRepository);
    }

    @Bean
    public CommercialCatalogPdfPort commercialCatalogPdfPort(MediaGateway media,
            PdfBranding branding) {
        return new CommercialCatalogPdfGenerator(media, branding);
    }

    @Bean
    public GenerateCommercialCatalogPdfUseCase generateCommercialCatalogPdfUseCase(
            CommercialCatalogRepositoryPort commercialCatalogRepositoryPort,
            CommercialCatalogPdfPort commercialCatalogPdfPort) {
        return new GenerateCommercialCatalogPdfUseCaseImpl(
                commercialCatalogRepositoryPort, commercialCatalogPdfPort);
    }
}
