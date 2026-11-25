package com.parsun.parsun_api.portal.application.services;

import com.parsun.parsun_api.partners.customers.warehouse_access.domain.entity.CustomerWarehouseAccess;
import com.parsun.parsun_api.partners.customers.warehouse_access.domain.repository.CustomerWarehouseAccessRepository;
import com.parsun.parsun_api.inventory.warehouses.stock.domain.entity.WarehouseStock;
import com.parsun.parsun_api.inventory.warehouses.stock.domain.repository.WarehouseStockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PortalAuthorizationServiceTest {

    @Mock
    private CustomerWarehouseAccessRepository customerWarehouseAccessRepository;

    @Mock
    private WarehouseStockRepository warehouseStockRepository;

    @Mock
    private CustomerWarehouseAccess warehouseAccess;

    @Mock
    private WarehouseStock warehouseStock;

    private PortalAuthorizationService service;
    private final UUID customerId = UUID.randomUUID();
    private final UUID otherCustomerId = UUID.randomUUID();
    private final UUID productId = UUID.randomUUID();
    private final UUID warehouseId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new PortalAuthorizationService(customerWarehouseAccessRepository, warehouseStockRepository);
        var authentication = new TestingAuthenticationToken(customerId.toString(), null);
        authentication.setAuthenticated(true);
        var context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void allowsResourcesOwnedByCurrentCustomer() {
        assertDoesNotThrow(() -> service.requireOwnership(customerId, "Quote"));
    }

    @Test
    void deniesResourcesOwnedByAnotherCustomer() {
        assertThrows(AuthorizationDeniedException.class, () -> service.requireOwnership(otherCustomerId, "Quote"));
    }

    @Test
    void allowsProductsWithAvailableStockInAssignedWarehouse() {
        when(warehouseAccess.getWarehouseId()).thenReturn(warehouseId);
        when(warehouseStock.getWarehouseId()).thenReturn(warehouseId);
        when(warehouseStock.getQuantity())
                .thenReturn(new java.math.BigDecimal("10"));
        when(warehouseStock.getReserved())
                .thenReturn(new java.math.BigDecimal("2"));
        when(customerWarehouseAccessRepository.findByCustomerId(customerId)).thenReturn(List.of(warehouseAccess));
        when(warehouseStockRepository.findByProductId(productId)).thenReturn(List.of(warehouseStock));

        assertDoesNotThrow(() -> service.requireProductAccess(productId));
    }

    @Test
    void deniesProductsOutsideAssignedWarehouses() {
        when(warehouseAccess.getWarehouseId()).thenReturn(warehouseId);
        when(warehouseStock.getWarehouseId()).thenReturn(UUID.randomUUID());
        when(customerWarehouseAccessRepository.findByCustomerId(customerId)).thenReturn(List.of(warehouseAccess));
        when(warehouseStockRepository.findByProductId(productId)).thenReturn(List.of(warehouseStock));

        assertThrows(AuthorizationDeniedException.class, () -> service.requireProductAccess(productId));
    }
}
