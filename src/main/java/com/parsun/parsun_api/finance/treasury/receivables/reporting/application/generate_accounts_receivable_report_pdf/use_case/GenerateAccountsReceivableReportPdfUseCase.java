package com.parsun.parsun_api.finance.treasury.receivables.reporting.application.generate_accounts_receivable_report_pdf.use_case;

import com.parsun.parsun_api.finance.treasury.receivables.reporting.application.dto.model.AccountsReceivableReportData;

import java.time.LocalDate;
import java.util.UUID;

public interface GenerateAccountsReceivableReportPdfUseCase {
    byte[] execute(UUID customerId, boolean overdueOnly, LocalDate dateFrom,
            LocalDate dateTo);
}
