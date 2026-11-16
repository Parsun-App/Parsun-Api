package com.cashly.cashly_api.portal.application.operations.use_case;

import com.cashly.cashly_api.sales.orders.application.update.request.UpdateOrderRequest;
import com.cashly.cashly_api.sales.orders.application.dto.response.OrderDetailResponse;
import com.cashly.cashly_api.sales.orders.application.update.use_case.UpdateOrderUseCase;
import com.cashly.cashly_api.portal.application.dto.request.UpdatePortalOrderRequest;
import com.cashly.cashly_api.portal.application.services.PortalAuthorizationService;

import java.util.List;
import java.util.UUID;

public class UpdatePortalOrderUseCase {

    private final UpdateOrderUseCase updateOrderUseCase;
    private final GetPortalOrderUseCase getPortalOrderUseCase;
    private final PortalAuthorizationService portalAuthorizationService;

    public UpdatePortalOrderUseCase(UpdateOrderUseCase updateOrderUseCase,
                                    GetPortalOrderUseCase getPortalOrderUseCase,
                                    PortalAuthorizationService portalAuthorizationService) {
        this.updateOrderUseCase = updateOrderUseCase;
        this.getPortalOrderUseCase = getPortalOrderUseCase;
        this.portalAuthorizationService = portalAuthorizationService;
    }

    public OrderDetailResponse execute(UUID id, UpdatePortalOrderRequest request) {
        getPortalOrderUseCase.execute(id);
        request.lines().forEach(line -> portalAuthorizationService.requireProductAccess(line.productId()));
        UpdateOrderRequest internalRequest = new UpdateOrderRequest(
                request.notes(),
                request.lines().stream()
                        .map(line -> {
                            UUID warehouseId = portalAuthorizationService
                                    .resolveWarehouse(line.productId(),
                                            line.quantity(),
                                            line.warehouseId());
                            return new UpdateOrderRequest.OrderLineRequest(
                                    line.productId(), line.quantity(),
                                    line.unitPrice(), List.of(
                                            new UpdateOrderRequest.WarehouseAllocationRequest(
                                                    warehouseId,
                                                    line.quantity())));
                        })
                        .toList()
        );

        return updateOrderUseCase.execute(id, internalRequest);
    }
}
