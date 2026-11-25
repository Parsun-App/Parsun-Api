package com.parsun.parsun_api.portal.application.operations.use_case;

import com.parsun.parsun_api.sales.invoices.invoices.application.dto.response.InvoiceDetailResponse;
import com.parsun.parsun_api.sales.invoices.invoices.application.get.use_case.GetInvoiceByIdUseCase;
import com.parsun.parsun_api.portal.application.services.PortalAuthorizationService;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public class GetPortalInvoiceUseCase {

    private final GetInvoiceByIdUseCase getInvoiceByIdUseCase;
    private final PortalAuthorizationService portalAuthorizationService;

    public GetPortalInvoiceUseCase(GetInvoiceByIdUseCase getInvoiceByIdUseCase,
                                    PortalAuthorizationService portalAuthorizationService) {
        this.getInvoiceByIdUseCase = getInvoiceByIdUseCase;
        this.portalAuthorizationService = portalAuthorizationService;
    }

    @Transactional(readOnly = true)
    public InvoiceDetailResponse execute(UUID id) {
        InvoiceDetailResponse invoice = getInvoiceByIdUseCase.execute(id);

        portalAuthorizationService.requireOwnership(invoice.customerId(), "Invoice");

        return invoice;
    }
}
