package com.parsun.parsun_api.portal.application.operations.use_case;

import com.parsun.parsun_api.finance.treasury.payments.collection.application.dto.response.PaymentResponse;
import com.parsun.parsun_api.finance.treasury.payments.collection.application.queries.use_case.GetPaymentByIdUseCase;
import com.parsun.parsun_api.portal.application.services.PortalAuthorizationService;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public class GetPortalPaymentUseCase {

    private final GetPaymentByIdUseCase getPaymentByIdUseCase;
    private final PortalAuthorizationService portalAuthorizationService;

    public GetPortalPaymentUseCase(GetPaymentByIdUseCase getPaymentByIdUseCase,
                                  PortalAuthorizationService portalAuthorizationService) {
        this.getPaymentByIdUseCase = getPaymentByIdUseCase;
        this.portalAuthorizationService = portalAuthorizationService;
    }

    @Transactional(readOnly = true)
    public PaymentResponse execute(UUID id) {
        var payment = getPaymentByIdUseCase.execute(id);
        portalAuthorizationService.requireOwnership(payment.customerId(), "Payment");
        return payment;
    }
}
