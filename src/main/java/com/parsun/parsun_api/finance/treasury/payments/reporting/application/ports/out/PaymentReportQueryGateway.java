package com.parsun.parsun_api.finance.treasury.payments.reporting.application.ports.out;
import com.parsun.parsun_api.finance.treasury.payments.reporting.application.dto.model.PaymentReportData;
import java.util.UUID;
public interface PaymentReportQueryGateway {
    PaymentReportData find(UUID id);
}
