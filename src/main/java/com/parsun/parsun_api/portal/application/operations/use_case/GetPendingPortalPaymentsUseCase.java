package com.parsun.parsun_api.portal.application.operations.use_case;

import com.parsun.parsun_api.finance.treasury.payments.collection.application.dto.response.PaymentListResponse;
import com.parsun.parsun_api.finance.treasury.payments.collection.application.mappers.PaymentMapper;
import com.parsun.parsun_api.finance.treasury.payments.collection.domain.entity.Payment;
import com.parsun.parsun_api.finance.treasury.payments.collection.domain.repository.PaymentRepository;
import com.parsun.parsun_api.portal.application.services.PortalAuthorizationService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public class GetPendingPortalPaymentsUseCase {

    private static final String STATUS_PENDING = "PENDING";

    private final PaymentRepository paymentRepository;
    private final PortalAuthorizationService portalAuthorizationService;

    public GetPendingPortalPaymentsUseCase(PaymentRepository paymentRepository,
                                            PortalAuthorizationService portalAuthorizationService) {
        this.paymentRepository = paymentRepository;
        this.portalAuthorizationService = portalAuthorizationService;
    }

    @Transactional(readOnly = true)
    public List<PaymentListResponse> execute(UUID invoiceId) {
        UUID customerId = portalAuthorizationService.getCurrentCustomerId();
        List<Payment> pendingPayments = paymentRepository.findAllWithFilters(null, null, customerId, STATUS_PENDING);

        return pendingPayments.stream()
                .filter(payment -> appliesToQuery(payment, invoiceId))
                .map(PaymentMapper::toListResponse)
                .toList();
    }

    private boolean appliesToQuery(Payment payment, UUID invoiceId) {
        if (invoiceId == null) {
            return true;
        }
        return invoiceId.equals(payment.getTargetInvoiceId());
    }
}
