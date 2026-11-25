package com.parsun.parsun_api.sales.orders.application.cancel.use_case;

import com.parsun.parsun_api.platform.notifications.application.ports.in.ActivityEvent;

import com.parsun.parsun_api.platform.notifications.application.ports.in.ActivityNotificationRecorder;
import com.parsun.parsun_api.platform.notifications.application.ports.in.CustomerNotificationRecorder;

import com.parsun.parsun_api.sales.orders.application.ports.out.OrderInventoryGateway;
import com.parsun.parsun_api.sales.orders.application.cancel.request.CancelOrderRequest;
import com.parsun.parsun_api.sales.orders.application.dto.response.OrderDetailResponse;
import com.parsun.parsun_api.sales.orders.application.mappers.OrderMapper;
import com.parsun.parsun_api.sales.orders.domain.entity.Order;
import com.parsun.parsun_api.sales.orders.domain.exceptions.OrderNotFoundException;
import com.parsun.parsun_api.sales.orders.domain.repository.OrderRepository;
import com.parsun.parsun_api.sales.orders.domain.valueobjects.OrderId;
import com.parsun.parsun_api.sales.orders.domain.valueobjects.OrderStatus;
import jakarta.transaction.Transactional;

import java.util.UUID;
import com.parsun.parsun_api.platform.audit.application.ports.in.Audited;

public class CancelOrderUseCase {

    private final OrderRepository orderRepository;
    private final OrderInventoryGateway inventory;

    private final CustomerNotificationRecorder customerNotifications;

    private final ActivityNotificationRecorder activityNotifications;

    public CancelOrderUseCase(OrderRepository orderRepository,
            OrderInventoryGateway inventory,
            ActivityNotificationRecorder activityNotifications) {
        this(orderRepository, inventory, activityNotifications, null);
    }

    public CancelOrderUseCase(OrderRepository orderRepository,
            OrderInventoryGateway inventory,
            ActivityNotificationRecorder activityNotifications,
            CustomerNotificationRecorder customerNotifications) {
        this.activityNotifications = activityNotifications != null
                ? activityNotifications
                : ActivityNotificationRecorder.NONE;
        this.orderRepository = orderRepository;
        this.inventory = inventory;
        this.customerNotifications = customerNotifications;
    }

    @Transactional
    @Audited(module = "sales", entityType = "ORDER", action = "CANCEL")
    public OrderDetailResponse execute(UUID id, CancelOrderRequest request) {
        Order order = orderRepository.findById(OrderId.of(id))
                .orElseThrow(() -> new OrderNotFoundException(id));

        OrderStatus previousStatus = order.getStatus();
        order.cancel(request.reason());
        revertStockAllocations(order, previousStatus);

        Order saved = orderRepository.save(order);
        activityNotifications.record(ActivityEvent.of("SALES_ORDERS",
                "CANCEL", "ORDER", saved.getId().value(),
                saved.getOrderNumber().value(), null));
        if (customerNotifications != null) {
            customerNotifications.orderCancelled(saved.getCustomerId(),
                    saved.getId().value(), request.reason());
        }
        return OrderMapper.toDetailResponse(saved);
    }

    private void revertStockAllocations(Order order,
            OrderStatus previousStatus) {
        boolean wasConfirmedOrInvoiced = previousStatus == OrderStatus.CONFIRMED
                || previousStatus == OrderStatus.INVOICED;

        var allocations = order.getLines().stream().flatMap(line -> line
                .getAllocations().stream()
                .map(allocation -> new OrderInventoryGateway.Allocation(
                        allocation.getWarehouseId(), line.getProductId(),
                        allocation.getAllocated())))
                .toList();
        if (wasConfirmedOrInvoiced) {
            inventory.restore(allocations,
                    new OrderInventoryGateway.Reference(order.getId().value(),
                            order.getOrderNumber().value(), "Order cancelled"));
        } else {
            inventory.release(allocations);
        }
    }
}
