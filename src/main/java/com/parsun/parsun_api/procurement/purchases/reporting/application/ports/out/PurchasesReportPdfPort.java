package com.parsun.parsun_api.procurement.purchases.reporting.application.ports.out;

import com.parsun.parsun_api.procurement.purchases.reporting.application.dto.model.PurchaseReportRow;

import java.util.List;

public interface PurchasesReportPdfPort {
    byte[] generatePdf(List<PurchaseReportRow> purchases, String mode);
}
