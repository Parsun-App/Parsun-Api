package com.parsun.parsun_api.portal.application.operations.use_case;

import com.parsun.parsun_api.finance.treasury.payments.collection.application.dto.response.PaymentListResponse;
import com.parsun.parsun_api.finance.treasury.payments.collection.application.queries.use_case.GetAllPaymentsUseCase;
import com.parsun.parsun_api.portal.application.dto.response.PortalPaymentSummaryResponse;
import com.parsun.parsun_api.portal.application.services.PortalAuthorizationService;
import com.parsun.parsun_api.shared.dto.PageResponse;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class ListPortalPaymentsUseCase {

    private final GetAllPaymentsUseCase getAllPaymentsUseCase;
    private final PortalAuthorizationService portalAuthorizationService;

    public ListPortalPaymentsUseCase(GetAllPaymentsUseCase getAllPaymentsUseCase,
                                      PortalAuthorizationService portalAuthorizationService) {
        this.getAllPaymentsUseCase = getAllPaymentsUseCase;
        this.portalAuthorizationService = portalAuthorizationService;
    }

    @Transactional(readOnly = true)
    public List<PaymentListResponse> execute(LocalDate dateFrom, LocalDate dateTo,
                                              String type, String status) {
        var customerId = portalAuthorizationService.getCurrentCustomerId();
        return getAllPaymentsUseCase.execute(dateFrom, dateTo, customerId, type, status);
    }

    @Transactional(readOnly = true)
    public PageResponse<PaymentListResponse> executePage(LocalDate dateFrom,
            LocalDate dateTo, String type, String status, String search,
            int page, int size, String orderBy, String orderDir) {
        var customerId = portalAuthorizationService.getCurrentCustomerId();
        return getAllPaymentsUseCase.executePage(dateFrom, dateTo, customerId,
                type, status, search, null, page, size, orderBy, orderDir);
    }

    @Transactional(readOnly = true)
    public PortalPaymentSummaryResponse getSummary(LocalDate dateFrom,
            LocalDate dateTo, String type, String status) {
        var customerId = portalAuthorizationService.getCurrentCustomerId();
        List<PaymentListResponse> payments = getAllPaymentsUseCase.execute(
                dateFrom, dateTo, customerId, type, status);
        BigDecimal appliedTotal = payments.stream()
                .filter(p -> "APPLIED".equals(p.status()))
                .map(PaymentListResponse::amount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal pendingTotal = payments.stream()
                .filter(p -> "PENDING".equals(p.status()))
                .map(PaymentListResponse::amount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long totalPayments = status == null || status.isBlank()
                ? payments.stream()
                        .filter(p -> "APPLIED".equals(p.status()))
                        .count()
                : payments.size();
        return new PortalPaymentSummaryResponse(appliedTotal, pendingTotal,
                totalPayments);
    }
}
