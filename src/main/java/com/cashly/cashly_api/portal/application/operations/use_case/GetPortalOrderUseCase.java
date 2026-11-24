package com.cashly.cashly_api.portal.application.operations.use_case;

import com.cashly.cashly_api.sales.orders.application.dto.response.OrderDetailResponse;
import com.cashly.cashly_api.sales.orders.application.get.use_case.GetOrderByIdUseCase;
import com.cashly.cashly_api.portal.application.services.PortalAuthorizationService;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public class GetPortalOrderUseCase {

    private final GetOrderByIdUseCase getOrderByIdUseCase;
    private final PortalAuthorizationService portalAuthorizationService;

    public GetPortalOrderUseCase(GetOrderByIdUseCase getOrderByIdUseCase,
                                 PortalAuthorizationService portalAuthorizationService) {
        this.getOrderByIdUseCase = getOrderByIdUseCase;
        this.portalAuthorizationService = portalAuthorizationService;
    }

    @Transactional(readOnly = true)
    public OrderDetailResponse execute(UUID id) {
        var order = getOrderByIdUseCase.execute(id);
        portalAuthorizationService.requireOwnership(order.customerId(), "Order");
        return order;
    }
}
