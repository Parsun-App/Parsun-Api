package com.cashly.cashly_api.portal.application.operations.use_case;

import com.cashly.cashly_api.platform.notifications.application.services.NotificationService;
import com.cashly.cashly_api.sales.orders.application.create.request.CreateOrderRequest;
import com.cashly.cashly_api.sales.orders.application.dto.response.OrderDetailResponse;
import com.cashly.cashly_api.sales.orders.application.create.use_case.CreateOrderUseCase;
import com.cashly.cashly_api.portal.application.dto.request.CreatePortalOrderRequest;
import com.cashly.cashly_api.portal.application.services.PortalAuthorizationService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public class CreatePortalOrderUseCase {

    private final CreateOrderUseCase createOrderUseCase;
    private final PortalAuthorizationService portalAuthorizationService;
    private final NotificationService notificationService;

    public CreatePortalOrderUseCase(CreateOrderUseCase createOrderUseCase,
                                     PortalAuthorizationService portalAuthorizationService,
                                     NotificationService notificationService) {
        this.createOrderUseCase = createOrderUseCase;
        this.portalAuthorizationService = portalAuthorizationService;
        this.notificationService = notificationService;
    }

    @Transactional
    public OrderDetailResponse execute(CreatePortalOrderRequest request) {
        UUID customerId = portalAuthorizationService.getCurrentCustomerId();
        request.lines().forEach(line -> portalAuthorizationService.requireProductAccess(line.productId()));

        CreateOrderRequest internalRequest = new CreateOrderRequest(
                customerId,
                request.notes(),
                request.lines().stream()
                        .map(line -> {
                            UUID warehouseId = portalAuthorizationService
                                    .resolveWarehouse(line.productId(),
                                            line.quantity(),
                                            line.warehouseId());
                            return new CreateOrderRequest.OrderLineRequest(
                                    line.productId(), line.quantity(),
                                    line.unitPrice(), List.of(
                                            new CreateOrderRequest.WarehouseAllocationRequest(
                                                    warehouseId,
                                                    line.quantity().intValue())));
                        })
                        .toList()
        );

        OrderDetailResponse response = createOrderUseCase.executeCustomerDraft(internalRequest);
        notificationService.notifyAdminsOrderCreated(response.id(), response.customerName());
        return response;
    }
}
