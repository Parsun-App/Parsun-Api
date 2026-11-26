package com.parsun.parsun_api.portal.application.operations.use_case;

import com.parsun.parsun_api.sales.orders.application.dto.response.OrderListResponse;
import com.parsun.parsun_api.sales.orders.application.list.use_case.GetOrdersUseCase;
import com.parsun.parsun_api.portal.application.services.PortalAuthorizationService;
import com.parsun.parsun_api.portal.application.dto.response.PortalOrderSummaryResponse;
import com.parsun.parsun_api.shared.dto.PageResponse;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ListPortalOrdersUseCase {

    private final GetOrdersUseCase getOrdersUseCase;
    private final PortalAuthorizationService portalAuthorizationService;

    public ListPortalOrdersUseCase(GetOrdersUseCase getOrdersUseCase,
                                    PortalAuthorizationService portalAuthorizationService) {
        this.getOrdersUseCase = getOrdersUseCase;
        this.portalAuthorizationService = portalAuthorizationService;
    }

    @Transactional(readOnly = true)
    public List<OrderListResponse> execute(LocalDate dateFrom, LocalDate dateTo,
                                            String status, UUID productId,
                                            UUID categoryId, String orderBy,
                                            String orderDir) {
        UUID customerId = portalAuthorizationService.getCurrentCustomerId();
        String effectiveStatus = status == null || status.isBlank()
                ? null
                : status;
        return getOrdersUseCase.execute(dateFrom, dateTo, customerId,
                effectiveStatus, productId, categoryId, orderBy, orderDir);
    }

    @Transactional(readOnly = true)
    public PageResponse<OrderListResponse> executePage(LocalDate dateFrom,
            LocalDate dateTo, String status, UUID productId, UUID categoryId,
            String search, int page, int size, String orderBy, String orderDir) {
        UUID customerId = portalAuthorizationService.getCurrentCustomerId();
        String effectiveStatus = status == null || status.isBlank()
                ? null
                : status;
        return getOrdersUseCase.executePage(dateFrom, dateTo, customerId,
                effectiveStatus, productId, categoryId, search, page, size,
                orderBy, orderDir, null);
    }

    @Transactional(readOnly = true)
    public PortalOrderSummaryResponse getSummary(LocalDate dateFrom,
            LocalDate dateTo, String status, UUID productId, UUID categoryId) {
        UUID customerId = portalAuthorizationService.getCurrentCustomerId();
        String effectiveStatus = status == null || status.isBlank()
                ? null
                : status;
        List<OrderListResponse> orders = getOrdersUseCase.execute(dateFrom,
                dateTo, customerId, effectiveStatus, productId, categoryId,
                null, null);
        long pendingCount = orders.stream()
                .filter(o -> "DRAFT".equals(o.status())
                        || "CUSTOMER_DRAFT".equals(o.status()))
                .count();
        BigDecimal totalAmount = orders.stream()
                .map(OrderListResponse::subtotal)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new PortalOrderSummaryResponse(pendingCount, totalAmount);
    }
}
