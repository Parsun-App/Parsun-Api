package com.cashly.cashly_api.portal.application.operations.use_case;

import com.cashly.cashly_api.sales.invoices.invoices.application.dto.response.InvoiceListResponse;
import com.cashly.cashly_api.sales.invoices.invoices.application.list.use_case.GetInvoicesUseCase;
import com.cashly.cashly_api.finance.treasury.payments.collection.domain.entity.Payment;
import com.cashly.cashly_api.finance.treasury.payments.collection.domain.repository.PaymentRepository;
import com.cashly.cashly_api.portal.application.dto.response.PortalInvoiceListResponse;
import com.cashly.cashly_api.portal.application.services.PortalAuthorizationService;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ListPortalInvoicesUseCase {

    private final GetInvoicesUseCase getInvoicesUseCase;
    private final PortalAuthorizationService portalAuthorizationService;
    private final PaymentRepository paymentRepository;

    public ListPortalInvoicesUseCase(GetInvoicesUseCase getInvoicesUseCase,
                                      PortalAuthorizationService portalAuthorizationService,
                                      PaymentRepository paymentRepository) {
        this.getInvoicesUseCase = getInvoicesUseCase;
        this.portalAuthorizationService = portalAuthorizationService;
        this.paymentRepository = paymentRepository;
    }

    @Transactional(readOnly = true)
    public List<PortalInvoiceListResponse> execute(LocalDate dateFrom, LocalDate dateTo,
                                                    List<String> statuses,
                                                    List<String> returnStatus,
                                                    UUID productId, UUID categoryId,
                                                    String orderBy, String orderDir) {
        UUID customerId = portalAuthorizationService.getCurrentCustomerId();
        List<String> effectiveStatuses = statuses == null || statuses.isEmpty()
                ? List.of("PENDING_PAYMENT", "PARTIALLY_PAID")
                : statuses;
        List<InvoiceListResponse> invoices = getInvoicesUseCase.execute(dateFrom, dateTo, customerId, effectiveStatuses,
                returnStatus, productId, categoryId, orderBy, orderDir);

        List<Payment> pendingPayments = paymentRepository.findAllWithFilters(null, null, customerId, "PENDING");
        Map<UUID, BigDecimal> pendingByInvoice = new HashMap<>();
        for (Payment p : pendingPayments) {
            if (p.getTargetInvoiceId() != null) {
                pendingByInvoice.merge(p.getTargetInvoiceId(), p.getAmount(),
                        BigDecimal::add);
            }
        }

        return invoices.stream()
                .map(inv -> new PortalInvoiceListResponse(
                        inv.id(), inv.invoiceNumber(), inv.invoiceNumber(), inv.createdAt(), null,
                        inv.status(), inv.total(), inv.subtotal(), 
                        inv.total().subtract(inv.paidAmount()), inv.paidAmount(),
                        inv.paymentType(), inv.createdAt(),
                        pendingByInvoice.getOrDefault(inv.id(), BigDecimal.ZERO)))
                .toList();
    }
}
