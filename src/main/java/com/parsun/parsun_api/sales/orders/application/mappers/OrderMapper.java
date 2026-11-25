package com.parsun.parsun_api.sales.orders.application.mappers;

import com.parsun.parsun_api.sales.orders.application.dto.response.OrderDetailResponse;
import com.parsun.parsun_api.sales.orders.application.dto.response.OrderListResponse;
import com.parsun.parsun_api.sales.orders.domain.entity.Order;
import com.parsun.parsun_api.sales.orders.domain.entity.OrderLine;
import com.parsun.parsun_api.sales.orders.application.ports.out.StockAvailabilityGateway;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class OrderMapper {

    public record WarehouseProductKey(UUID warehouseId, UUID productId) {
    }

    public record WarehouseEnrichment(String code, String name,
            java.math.BigDecimal availableStock) {
    }

    private OrderMapper() {
    }

    public static OrderDetailResponse toDetailResponse(Order order,
            Map<WarehouseProductKey, WarehouseEnrichment> enrichmentMap,
            Map<UUID, List<StockAvailabilityGateway.WarehouseStockInfo>> productStocks,
            Map<WarehouseProductKey, java.math.BigDecimal> thisOrderAllocations,
            java.math.BigDecimal creditLimit,
            java.math.BigDecimal availableCredit) {
        List<OrderDetailResponse.OrderLineDetailResponse> lineResponses = order
                .getLines().stream().map(line -> toLineDetailResponse(line,
                        enrichmentMap, productStocks, thisOrderAllocations))
                .toList();

        return new OrderDetailResponse(order.getId().value(),
                order.getOrderNumber().value(), order.getCustomerId(),
                order.getCustomerName(), order.getCustomerRif(),
                order.getCreatedBy(), order.getCreatedByName(),
                order.getStatus().name(), order.getNotes(), order.getSubtotal(),
                order.getInvoiceId(), order.getInvoiceNumber(),
                order.getCancelledAt(), order.getCancelledReason(),
                order.getCreatedAt(), order.getUpdatedAt(), lineResponses,
                creditLimit, availableCredit);
    }

    public static OrderDetailResponse toDetailResponse(Order order,
            Map<WarehouseProductKey, WarehouseEnrichment> enrichmentMap,
            Map<UUID, List<StockAvailabilityGateway.WarehouseStockInfo>> productStocks) {
        return toDetailResponse(order, enrichmentMap, productStocks, Map.of());
    }

    public static OrderDetailResponse toDetailResponse(Order order,
            Map<WarehouseProductKey, WarehouseEnrichment> enrichmentMap,
            Map<UUID, List<StockAvailabilityGateway.WarehouseStockInfo>> productStocks,
            Map<WarehouseProductKey, java.math.BigDecimal> thisOrderAllocations) {
        return toDetailResponse(order, enrichmentMap, productStocks,
                thisOrderAllocations, null, null);
    }

    public static OrderDetailResponse toDetailResponse(Order order) {
        return toDetailResponse(order, Map.of(), Map.of(), Map.of());
    }

    public static OrderListResponse toListResponse(Order order) {
        return new OrderListResponse(order.getId().value(),
                order.getOrderNumber().value(), order.getCustomerId(),
                order.getCustomerName(), order.getStatus().name(),
                order.getSubtotal(), order.getLines().size(),
                order.getInvoiceId(), order.getCreatedAt(),
                stockAssigned(order));
    }

    private static boolean stockAssigned(Order order) {
        return !order.getLines().isEmpty() && order.getLines().stream()
                .allMatch(line -> !line.getAllocations().isEmpty()
                        && line.getAllocations().stream()
                                .map(com.parsun.parsun_api.sales.orders.domain.entity.OrderLineAllocation::getAllocated)
                                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add)
                                .compareTo(line.getQuantity()) == 0);
    }

    private static OrderDetailResponse.OrderLineDetailResponse toLineDetailResponse(
            OrderLine line,
            Map<WarehouseProductKey, WarehouseEnrichment> enrichmentMap,
            Map<UUID, List<StockAvailabilityGateway.WarehouseStockInfo>> productStocks,
            Map<WarehouseProductKey, java.math.BigDecimal> thisOrderAllocations) {

        List<OrderDetailResponse.OrderLineDetailResponse.AllocationDetail> allocationDetails = line
                .getAllocations().stream().map(a -> {
                    WarehouseProductKey key = new WarehouseProductKey(
                            a.getWarehouseId(), line.getProductId());
                    WarehouseEnrichment enrichment = enrichmentMap.get(key);
                    String code = enrichment != null ? enrichment.code() : null;
                    String name = enrichment != null ? enrichment.name() : null;
                    java.math.BigDecimal availableStock = enrichment != null
                            ? enrichment.availableStock()
                            : java.math.BigDecimal.ZERO;
                    return new OrderDetailResponse.OrderLineDetailResponse.AllocationDetail(
                            a.getWarehouseId(), code, name, a.getAllocated(),
                            availableStock);
                }).toList();

        List<OrderDetailResponse.WarehouseStockDetail> stockDetails = productStocks
                .getOrDefault(line.getProductId(), List.of()).stream()
                .map(s -> {
                    WarehouseProductKey key = new WarehouseProductKey(
                            s.warehouseId(), line.getProductId());
                    java.math.BigDecimal thisAllocation = thisOrderAllocations
                            .getOrDefault(key, java.math.BigDecimal.ZERO);
                    return new OrderDetailResponse.WarehouseStockDetail(
                            s.warehouseId(), s.warehouseCode(),
                            s.warehouseName(), s.quantity(),
                            s.reserved().subtract(thisAllocation)
                                    .max(java.math.BigDecimal.ZERO),
                            s.available().add(thisAllocation));
                }).toList();

        return new OrderDetailResponse.OrderLineDetailResponse(
                line.getId().value(), line.getProductId(),
                line.getProductName(), line.getProductCode(),
                line.getQuantity(), line.getUnitPrice(), line.getLineTotal(),
                allocationDetails, stockDetails);
    }
}
