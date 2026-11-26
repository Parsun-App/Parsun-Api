package com.parsun.parsun_api.finance.treasury.cash_register.reporting.application.ports.out;

import com.parsun.parsun_api.finance.treasury.cash_register.reporting.application.dto.model.CashRegisterExportRow;
import com.parsun.parsun_api.finance.treasury.cash_register.reporting.application.dto.model.CashRegisterReportFilter;
import com.parsun.parsun_api.finance.treasury.cash_register.reporting.application.dto.model.ReportFormat;
import com.parsun.parsun_api.finance.treasury.cash_register.reporting.application.dto.model.ReportType;

import java.util.List;

public interface CashRegisterRangeReportPort {
    byte[] generate(List<CashRegisterExportRow> rows,
            CashRegisterReportFilter filter, ReportFormat format,
            ReportType type);
}
