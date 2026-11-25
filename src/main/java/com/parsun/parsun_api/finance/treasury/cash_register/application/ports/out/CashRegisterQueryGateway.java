package com.parsun.parsun_api.finance.treasury.cash_register.application.ports.out;

import com.parsun.parsun_api.finance.treasury.cash_register.reporting.application.dto.model.CashRegisterExportRow;
import com.parsun.parsun_api.finance.treasury.cash_register.reporting.application.dto.model.CashRegisterReportFilter;
import com.parsun.parsun_api.finance.treasury.cash_register.reporting.application.dto.model.CustomerSummary;
import com.parsun.parsun_api.finance.treasury.cash_register.sessions.application.dto.response.DailyTotals;
import com.parsun.parsun_api.finance.treasury.cash_register.sessions.domain.entity.CashRegister;
import com.parsun.parsun_api.finance.treasury.cash_register.sessions.domain.entity.CashRegisterDetail;
import com.parsun.parsun_api.finance.treasury.cash_register.sessions.domain.valueobjects.RegisterId;
import com.parsun.parsun_api.shared.dto.PageResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface CashRegisterQueryGateway {
    PageResponse<CashRegister> findAll(int page, int size, String sort,
            LocalDate dateFrom, LocalDate dateTo, UUID registerId);
    DailyTotals findDailyTotals(LocalDate closureDate);
    DailyTotals findSessionTotals(RegisterId registerId);
    DailyTotals findRangeTotals(LocalDate dateFrom, LocalDate dateTo);
    List<CashRegisterExportRow> findExportRows(LocalDate dateFrom,
            LocalDate dateTo);
    List<CashRegisterExportRow> findExportRows(CashRegisterReportFilter filter);
    List<CashRegisterDetail> findPaymentMethodSummary(LocalDate dateFrom,
            LocalDate dateTo);
    List<CustomerSummary> findCustomerSummary(LocalDate dateFrom,
            LocalDate dateTo);
}
