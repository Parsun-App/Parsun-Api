package com.parsun.parsun_api.finance.treasury.cash_register.reporting.application.ports.out;

import com.parsun.parsun_api.finance.treasury.cash_register.sessions.application.dto.response.CashRegisterResponse;
import com.parsun.parsun_api.finance.treasury.cash_register.reporting.application.dto.model.ReportType;

public interface CashRegisterReportPort {

    byte[] generateExcel(CashRegisterResponse register, ReportType type);

    byte[] generatePdf(CashRegisterResponse register, ReportType type);
}
