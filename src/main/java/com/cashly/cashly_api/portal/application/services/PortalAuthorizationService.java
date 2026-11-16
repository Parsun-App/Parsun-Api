package com.cashly.cashly_api.portal.application.services;

import com.cashly.cashly_api.partners.customers.warehouse_access.domain.entity.CustomerWarehouseAccess;
import com.cashly.cashly_api.partners.customers.warehouse_access.domain.repository.CustomerWarehouseAccessRepository;
import com.cashly.cashly_api.inventory.warehouses.stock.domain.entity.WarehouseStock;
import com.cashly.cashly_api.inventory.warehouses.stock.domain.repository.WarehouseStockRepository;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class PortalAuthorizationService {

    private final CustomerWarehouseAccessRepository customerWarehouseAccessRepository;
    private final WarehouseStockRepository warehouseStockRepository;

    public PortalAuthorizationService(CustomerWarehouseAccessRepository customerWarehouseAccessRepository,
                                      WarehouseStockRepository warehouseStockRepository) {
        this.customerWarehouseAccessRepository = customerWarehouseAccessRepository;
        this.warehouseStockRepository = warehouseStockRepository;
    }

    public UUID getCurrentCustomerId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated customer found");
        }
        String principal = (String) authentication.getPrincipal();
        return UUID.fromString(principal);
    }

    public void requireOwnership(UUID resourceCustomerId, String resourceName) {
        if (!getCurrentCustomerId().equals(resourceCustomerId)) {
            throw new AuthorizationDeniedException(resourceName + " does not belong to current customer");
        }
    }

    public void requireProductAccess(UUID productId) {
        UUID customerId = getCurrentCustomerId();
        var allowedWarehouseIds = allowedWarehouseIds(customerId);
        boolean available = warehouseStockRepository.findByProductId(productId).stream()
                .anyMatch(stock -> allowedWarehouseIds.contains(stock.getWarehouseId())
                        && stock.getQuantity() - stock.getReserved() > 0);
        if (!available) {
            throw new AuthorizationDeniedException("Product is not available in an assigned warehouse");
        }
    }

    public UUID resolveWarehouse(UUID productId, BigDecimal quantity,
            UUID requestedWarehouseId) {
        UUID customerId = getCurrentCustomerId();
        var allowedWarehouseIds = allowedWarehouseIds(customerId);
        if (allowedWarehouseIds.isEmpty()) {
            throw new AuthorizationDeniedException(
                    "Customer has no assigned warehouses");
        }
        int required = quantity.intValue();
        if (required <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero");
        }

        List<WarehouseStock> stocks = warehouseStockRepository
                .findByProductId(productId).stream()
                .filter(stock -> allowedWarehouseIds
                        .contains(stock.getWarehouseId()))
                .toList();

        if (requestedWarehouseId != null) {
            boolean allowed = allowedWarehouseIds.contains(requestedWarehouseId);
            WarehouseStock stock = stocks.stream()
                    .filter(item -> requestedWarehouseId
                            .equals(item.getWarehouseId()))
                    .findFirst().orElse(null);
            if (!allowed || stock == null
                    || stock.getQuantity() - stock.getReserved() < required) {
                throw new AuthorizationDeniedException(
                        "Selected warehouse does not have enough stock for this product");
            }
            return requestedWarehouseId;
        }

        return stocks.stream()
                .filter(stock -> stock.getQuantity() - stock.getReserved() >= required)
                .max(Comparator.comparingInt(
                        stock -> stock.getQuantity() - stock.getReserved()))
                .map(WarehouseStock::getWarehouseId)
                .orElseThrow(() -> new AuthorizationDeniedException(
                        "Not enough stock in the customer's assigned warehouses"));
    }

    public void requireWarehouseAccess(UUID warehouseId) {
        if (!allowedWarehouseIds(getCurrentCustomerId()).contains(warehouseId)) {
            throw new AuthorizationDeniedException(
                    "Warehouse is not assigned to the current customer");
        }
    }

    private Set<UUID> allowedWarehouseIds(UUID customerId) {
        return customerWarehouseAccessRepository.findByCustomerId(customerId)
                .stream().map(CustomerWarehouseAccess::getWarehouseId)
                .collect(Collectors.toSet());
    }
}
