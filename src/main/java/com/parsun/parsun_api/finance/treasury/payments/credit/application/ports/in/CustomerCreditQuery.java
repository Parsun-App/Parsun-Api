package com.parsun.parsun_api.finance.treasury.payments.credit.application.ports.in;
import java.util.List;
import java.util.UUID;
public interface CustomerCreditQuery {
    CreditStatusSnapshot getCreditStatus(UUID customerId);
    List<OverdueInvoiceSnapshot> findAllOverdueInvoices();
}
