package com.cashly.cashly_api.portal.application.operations.use_case;

import com.cashly.cashly_api.portal.application.services.PortalAuthorizationService;
import com.cashly.cashly_api.sales.orders.application.dto.response.OrderDetailResponse;
import com.cashly.cashly_api.sales.orders.application.update.request.UpdateOrderRequest;
import com.cashly.cashly_api.sales.orders.application.update.use_case.UpdateOrderUseCase;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public class ReassignPortalOrderWarehouseUseCase {

    private final UpdateOrderUseCase updateOrderUseCase;
    private final GetPortalOrderUseCase getPortalOrderUseCase;
    private final PortalAuthorizationService portalAuthorizationService;

    public ReassignPortalOrderWarehouseUseCase(
            UpdateOrderUseCase updateOrderUseCase,
            GetPortalOrderUseCase getPortalOrderUseCase,
            PortalAuthorizationService portalAuthorizationService) {
        this.updateOrderUseCase = updateOrderUseCase;
        this.getPortalOrderUseCase = getPortalOrderUseCase;
        this.portalAuthorizationService = portalAuthorizationService;
    }

    @Transactional
    public OrderDetailResponse execute(UUID orderId, UUID warehouseId) {
        OrderDetailResponse order = getPortalOrderUseCase.execute(orderId);
        portalAuthorizationService.requireWarehouseAccess(warehouseId);

        UpdateOrderRequest request = new UpdateOrderRequest(order.notes(),
                order.lines().stream()
                        .map(line -> new UpdateOrderRequest.OrderLineRequest(
                                line.productId(), line.quantity(),
                                line.unitPrice(), List.of(
                                        new UpdateOrderRequest.WarehouseAllocationRequest(
                                                warehouseId,
                                                line.quantity()))))
                        .toList());
        return updateOrderUseCase.execute(orderId, request);
    }
}
