package com.parsun.parsun_api.finance.treasury.payments.reporting.application.ports.out;

import com.parsun.parsun_api.finance.treasury.payments.reporting.application.dto.model.PaymentHistoryReportData;

public interface PaymentHistoryReportPdfPort {
    byte[] generateReport(PaymentHistoryReportData data);
}
