package com.parsun.parsun_api.finance.treasury.receivables.application.ports.out;

import com.parsun.parsun_api.finance.treasury.receivables.application.ports.out.model.AccountsReceivableRow;

import com.parsun.parsun_api.finance.treasury.receivables.application.dto.response.CustomerDebtListResponse;
import com.parsun.parsun_api.finance.treasury.receivables.application.dto.response.CustomerDebtSummaryResponse;
import com.parsun.parsun_api.finance.treasury.receivables.application.dto.response.CustomerInvoiceDebtResponse;
import com.parsun.parsun_api.finance.treasury.receivables.application.dto.response.PendingPaymentResponse;
import com.parsun.parsun_api.finance.treasury.receivables.application.dto.response.ReceivablesSummaryResponse;
import com.parsun.parsun_api.shared.dto.PageResponse;

import java.time.LocalDate;
import java.util.List;

import java.util.UUID;

public interface ReceivablesQueryGateway {

    ReceivablesSummaryResponse findSummary();

    PageResponse<CustomerDebtListResponse> findCustomersWithDebt(String search,
            int page, int size, String sort);
    PageResponse<CustomerDebtListResponse> findCustomersWithDebt(String search,
            int page, int size, String sort,
            com.parsun.parsun_api.finance.treasury.receivables.application.customer_debt.use_case.CustomerDebtAction action);

    CustomerDebtSummaryResponse findCustomerDebt(UUID customerId);

    PageResponse<CustomerInvoiceDebtResponse> findCustomerInvoices(
            UUID customerId, String search, String status, int page, int size);

    PageResponse<PendingPaymentResponse> findPaymentHistory(String search,
            int page, int size);

    PageResponse<PendingPaymentResponse> findCustomerPaymentHistory(
            UUID customerId, UUID invoiceId, int page, int size);

    List<AccountsReceivableRow> findAccountsReceivableForReport(UUID customerId,
            boolean overdueOnly, LocalDate dateFrom, LocalDate dateTo);
}
