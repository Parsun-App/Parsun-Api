package com.parsun.parsun_api.portal.application.operations.use_case;

import com.parsun.parsun_api.sales.orders.application.dto.response.OrderListResponse;
import com.parsun.parsun_api.sales.orders.application.list.use_case.GetOrdersUseCase;
import com.parsun.parsun_api.portal.application.services.PortalAuthorizationService;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
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
}
