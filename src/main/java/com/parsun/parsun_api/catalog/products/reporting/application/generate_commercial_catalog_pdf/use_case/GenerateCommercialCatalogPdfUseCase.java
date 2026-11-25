package com.parsun.parsun_api.catalog.products.reporting.application.generate_commercial_catalog_pdf.use_case;

import com.parsun.parsun_api.catalog.products.reporting.application.dto.query.model.CommercialCatalogQuery;

public interface GenerateCommercialCatalogPdfUseCase {
    byte[] execute(CommercialCatalogQuery query);
}
