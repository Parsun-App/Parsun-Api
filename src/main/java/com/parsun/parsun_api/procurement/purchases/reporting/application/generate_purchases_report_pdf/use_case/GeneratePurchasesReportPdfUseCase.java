package com.parsun.parsun_api.procurement.purchases.reporting.application.generate_purchases_report_pdf.use_case;

import com.parsun.parsun_api.procurement.purchases.reporting.application.dto.model.PurchaseReportFilter;

public interface GeneratePurchasesReportPdfUseCase {
    byte[] execute(PurchaseReportFilter filter, String mode);
}
