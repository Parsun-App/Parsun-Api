package com.parsun.parsun_api.finance.treasury.payments.reporting.application.ports.out;

import com.parsun.parsun_api.finance.treasury.payments.reporting.application.dto.model.PaymentReportData;

public interface PaymentPdfPort {
    byte[] generatePaymentPdf(PaymentReportData payment);
}
