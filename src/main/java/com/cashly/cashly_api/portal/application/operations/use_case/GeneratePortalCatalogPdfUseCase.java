package com.cashly.cashly_api.portal.application.operations.use_case;

import com.cashly.cashly_api.portal.application.dto.response.PortalProductResponse;
import com.cashly.cashly_api.catalog.products.reporting.application.dto.response.CommercialCatalogResponse;
import com.cashly.cashly_api.catalog.products.reporting.application.ports.out.CommercialCatalogPdfPort;
import com.cashly.cashly_api.shared.dto.PageResponse;

import java.util.List;
import java.util.UUID;

public class GeneratePortalCatalogPdfUseCase {

    private static final int ALL_PRODUCTS_SIZE = 10000;

    private final ListProductsUseCase listProductsUseCase;
    private final CommercialCatalogPdfPort commercialCatalogPdfPort;

    public GeneratePortalCatalogPdfUseCase(
            ListProductsUseCase listProductsUseCase,
            CommercialCatalogPdfPort commercialCatalogPdfPort) {
        this.listProductsUseCase = listProductsUseCase;
        this.commercialCatalogPdfPort = commercialCatalogPdfPort;
    }

    public byte[] execute(UUID customerId) {
        PageResponse<PortalProductResponse> page = listProductsUseCase.execute(
                customerId, null, null, 0, ALL_PRODUCTS_SIZE);

        List<CommercialCatalogResponse> items = page.content().stream()
                .map(this::toCatalogResponse)
                .toList();

        return commercialCatalogPdfPort.generatePdf(items);
    }

    private CommercialCatalogResponse toCatalogResponse(
            PortalProductResponse product) {
        return new CommercialCatalogResponse(
                UUID.fromString(product.id()), product.name(), product.code(),
                product.description(), product.photo(),
                product.categoryId() == null
                        ? null
                        : UUID.fromString(product.categoryId()),
                product.categoryName(), product.price(), null);
    }
}
