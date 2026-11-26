package com.parsun.parsun_api.finance.treasury.cash_register.reporting.application.excel.use_case;

import com.parsun.parsun_api.finance.treasury.cash_register.reporting.application.dto.model.ReportType;

import java.time.LocalDate;

public interface GenerateCashRegisterExcelUseCase {
    byte[] execute(LocalDate dateFrom, LocalDate dateTo, ReportType type);
}
