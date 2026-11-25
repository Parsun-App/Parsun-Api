package com.parsun.parsun_api.procurement.purchases.reporting.application.ports.out;

import com.parsun.parsun_api.procurement.purchases.reporting.application.dto.model.PurchaseReportData;

public interface PurchasePdfPort {
    byte[] generatePdf(PurchaseReportData purchase);
}
