package com.parsun.parsun_api.finance.treasury.receivables.reporting.application.ports.out;
import com.parsun.parsun_api.finance.treasury.receivables.reporting.application.dto.model.AccountsReceivableReportData.AccountReceivableRow;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
public interface AccountsReceivableQueryGateway {
    List<AccountReceivableRow> findAccountsReceivableForReport(UUID customerId,
            boolean overdueOnly, LocalDate dateFrom, LocalDate dateTo);
}