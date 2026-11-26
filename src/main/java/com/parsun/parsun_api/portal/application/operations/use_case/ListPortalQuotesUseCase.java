package com.parsun.parsun_api.portal.application.operations.use_case;

import com.parsun.parsun_api.portal.application.services.PortalAuthorizationService;
import com.parsun.parsun_api.portal.application.dto.response.PortalQuoteSummaryResponse;
import com.parsun.parsun_api.sales.quotes.application.dto.response.QuoteListResponse;
import com.parsun.parsun_api.sales.quotes.application.list.use_case.GetQuotesUseCase;
import com.parsun.parsun_api.shared.dto.PageResponse;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ListPortalQuotesUseCase {

    private final GetQuotesUseCase getQuotesUseCase;
    private final PortalAuthorizationService portalAuthorizationService;

    public ListPortalQuotesUseCase(GetQuotesUseCase getQuotesUseCase,
                                    PortalAuthorizationService portalAuthorizationService) {
        this.getQuotesUseCase = getQuotesUseCase;
        this.portalAuthorizationService = portalAuthorizationService;
    }

    @Transactional(readOnly = true)
    public List<QuoteListResponse> execute(LocalDate dateFrom, LocalDate dateTo,
                                            String status, UUID productId,
                                            UUID categoryId, String orderBy,
                                            String orderDir) {
        UUID customerId = portalAuthorizationService.getCurrentCustomerId();
        String effectiveStatus = status == null || status.isBlank()
                ? null
                : status;
        return getQuotesUseCase.execute(dateFrom, dateTo, customerId,
                effectiveStatus, productId, categoryId, orderBy, orderDir);
    }

    @Transactional(readOnly = true)
    public PageResponse<QuoteListResponse> executePage(LocalDate dateFrom,
            LocalDate dateTo, String status, UUID productId, UUID categoryId,
            String search, int page, int size, String orderBy, String orderDir) {
        UUID customerId = portalAuthorizationService.getCurrentCustomerId();
        String effectiveStatus = status == null || status.isBlank()
                ? null
                : status;
        return getQuotesUseCase.executePage(dateFrom, dateTo, customerId,
                effectiveStatus, productId, categoryId, search, page, size,
                orderBy, orderDir, null);
    }

    @Transactional(readOnly = true)
    public PortalQuoteSummaryResponse getSummary(LocalDate dateFrom,
            LocalDate dateTo, String status, UUID productId, UUID categoryId) {
        UUID customerId = portalAuthorizationService.getCurrentCustomerId();
        String effectiveStatus = status == null || status.isBlank()
                ? null
                : status;
        List<QuoteListResponse> quotes = getQuotesUseCase.execute(dateFrom,
                dateTo, customerId, effectiveStatus, productId, categoryId,
                null, null);
        long pendingCount = quotes.stream()
                .filter(q -> "DRAFT".equals(q.status())
                        || "CUSTOMER_DRAFT".equals(q.status()))
                .count();
        BigDecimal totalAmount = quotes.stream()
                .map(QuoteListResponse::subtotal)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new PortalQuoteSummaryResponse(pendingCount, totalAmount);
    }
}
