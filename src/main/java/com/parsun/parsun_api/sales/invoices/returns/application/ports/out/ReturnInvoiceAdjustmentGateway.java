package com.parsun.parsun_api.sales.invoices.returns.application.ports.out;

import com.parsun.parsun_api.sales.invoices.returns.domain.entity.Return;

import java.math.BigDecimal;

public interface ReturnInvoiceAdjustmentGateway {
    void apply(Return returned);
    void reverse(Return returned, BigDecimal totalPaid,
            BigDecimal totalDebitNotes, BigDecimal totalCreditNotes);
}
