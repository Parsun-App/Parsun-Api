package com.parsun.parsun_api.sales.invoices.invoices.infrastructure.integration;
import com.parsun.parsun_api.sales.invoices.invoices.application.ports.out.InvoicePaymentSettlementGateway;
import com.parsun.parsun_api.finance.treasury.payments.collection.application.ports.in.CashInvoiceSettlement;
import org.springframework.stereotype.Component;
@Component
public class LocalInvoicePaymentSettlementGateway
        implements
            InvoicePaymentSettlementGateway {
    private final CashInvoiceSettlement settlement;
    public LocalInvoicePaymentSettlementGateway(
            CashInvoiceSettlement settlement) {
        this.settlement = settlement;
    }
    public void settleCashInvoice(Settlement command) {
        settlement
                .settle(new CashInvoiceSettlement.Command(command.customerId(),
                        command.invoiceId(), command.invoiceNumber(),
                        command.amount(), command.paymentMethodId(),
                        command.actorId(), command.actorName()));
    }
}
