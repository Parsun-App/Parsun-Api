package com.parsun.parsun_api.finance.treasury.cash_register.sessions.application.ports.out;

import com.parsun.parsun_api.finance.treasury.cash_register.sessions.application.expenses.model.ExpenseFilter;
import com.parsun.parsun_api.finance.treasury.cash_register.sessions.application.expenses.response.ExpenseResponse;
import com.parsun.parsun_api.shared.dto.PageResponse;

public interface ExpenseQueryGateway {
    PageResponse<ExpenseResponse> findExpenses(ExpenseFilter filter);
}
