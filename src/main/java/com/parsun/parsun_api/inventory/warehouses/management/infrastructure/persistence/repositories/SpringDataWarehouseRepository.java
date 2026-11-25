package com.parsun.parsun_api.inventory.warehouses.management.infrastructure.persistence.repositories;

import com.parsun.parsun_api.inventory.warehouses.management.infrastructure.persistence.entities.WarehouseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;
import java.util.UUID;

public interface SpringDataWarehouseRepository
        extends
            JpaRepository<WarehouseEntity, UUID> {

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, UUID id);

    @Query("""
            SELECT w FROM WarehouseEntity w
            WHERE (:search IS NULL OR LOWER(w.name) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(w.code) LIKE LOWER(CONCAT('%', :search, '%')))
            AND (:active IS NULL OR w.active = :active)
            """)
    Page<WarehouseEntity> findAllWithFilters(@Param("search") String search,
            @Param("active") Boolean active, Pageable pageable);

    @Query(value = """
            SELECT BIN_TO_UUID(ws.warehouse_id) FROM warehouse_stock ws
            WHERE ws.warehouse_id IN :ids AND ws.quantity > 0
            """, nativeQuery = true)
    Set<UUID> findIdsWithActiveStock(@Param("ids") Set<UUID> ids);

    @Query(value = """
            SELECT BIN_TO_UUID(x.warehouse_id) FROM (
                SELECT warehouse_id FROM warehouse_stock WHERE warehouse_id IN :ids
                UNION SELECT warehouse_id FROM stock_movements WHERE warehouse_id IN :ids
                UNION SELECT warehouse_id FROM customer_warehouse_access WHERE warehouse_id IN :ids
                UNION SELECT warehouse_id FROM order_line_allocations WHERE warehouse_id IN :ids
                UNION SELECT warehouse_id FROM quote_allocations WHERE warehouse_id IN :ids
                UNION SELECT warehouse_id FROM invoice_line_allocations WHERE warehouse_id IN :ids
                UNION SELECT warehouse_id FROM return_line_allocations WHERE warehouse_id IN :ids
                UNION SELECT warehouse_id FROM purchase_line_allocations WHERE warehouse_id IN :ids
            ) x
            """, nativeQuery = true)
    Set<UUID> findIdsWithAssociatedRecords(@Param("ids") Set<UUID> ids);
}
