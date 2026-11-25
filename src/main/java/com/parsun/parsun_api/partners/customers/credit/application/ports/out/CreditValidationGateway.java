package com.parsun.parsun_api.partners.customers.credit.application.ports.out;
import com.parsun.parsun_api.partners.customers.credit.application.dto.response.CreditStatusResponse;
import com.parsun.parsun_api.partners.customers.credit.application.dto.response.OverdueInvoiceResponse;
import java.util.List;
import java.util.UUID;
public interface CreditValidationGateway {
    CreditStatusResponse getCreditStatus(UUID customerId);
    List<OverdueInvoiceResponse> findAllOverdueInvoices();
}
