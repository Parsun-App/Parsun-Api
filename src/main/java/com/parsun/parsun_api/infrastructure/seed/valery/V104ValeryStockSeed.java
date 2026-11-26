package com.parsun.parsun_api.infrastructure.seed.valery;

import com.parsun.parsun_api.catalog.products.infrastructure.persistence.entities.ProductEntity;
import com.parsun.parsun_api.infrastructure.seed.SeedSource;
import com.parsun.parsun_api.infrastructure.seed.SeedStep;
import com.parsun.parsun_api.inventory.warehouses.management.infrastructure.persistence.entities.WarehouseEntity;
import com.parsun.parsun_api.inventory.warehouses.stock.domain.valueobjects.MovementType;
import com.parsun.parsun_api.inventory.warehouses.stock.domain.valueobjects.ReferenceType;
import com.parsun.parsun_api.inventory.warehouses.stock.infrastructure.persistence.entities.StockMovementEntity;
import com.parsun.parsun_api.inventory.warehouses.stock.infrastructure.persistence.entities.WarehouseStockEntity;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class V104ValeryStockSeed implements SeedStep {

    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2025, 12,
            31, 23, 0);

    private final EntityManager entityManager;

    public V104ValeryStockSeed(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public int version() {
        return 104;
    }

    @Override
    public SeedSource source() {
        return SeedSource.VALERY;
    }

    @Override
    public void seed() {
        for (String[] row : ValeryCsv.load("stock")) {
            String productCode = ValeryCsv.text(row, 0);
            String warehouseCode = ValeryCsv.text(row, 1);
            BigDecimal quantity = ValeryCsv.decimal(row, 2)
                    .setScale(4, RoundingMode.HALF_UP);
            if (productCode.isEmpty() || warehouseCode.isEmpty()
                    || quantity.signum() == 0) {
                continue;
            }
            UUID productId = ValeryIds.product(productCode);
            UUID warehouseId = ValeryIds.warehouse(warehouseCode);
            if (entityManager.find(ProductEntity.class, productId) == null
                    || entityManager.find(WarehouseEntity.class,
                            warehouseId) == null) {
                continue;
            }
            persistStock(productCode, warehouseCode, productId, warehouseId,
                    quantity);
            persistMovement(productCode, warehouseCode, productId, warehouseId,
                    quantity);
        }
    }

    private void persistStock(String productCode, String warehouseCode,
            UUID productId, UUID warehouseId, BigDecimal quantity) {
        UUID id = ValeryIds.stock(productCode, warehouseCode);
        if (entityManager.find(WarehouseStockEntity.class, id) != null) {
            return;
        }
        var entity = new WarehouseStockEntity();
        entity.setId(id);
        entity.setWarehouseId(warehouseId);
        entity.setProductId(productId);
        entity.setQuantity(quantity);
        entity.setReserved(BigDecimal.ZERO);
        entity.setLastMovementAt(CREATED_AT);
        entity.setUpdatedAt(CREATED_AT);
        entityManager.persist(entity);
    }

    private void persistMovement(String productCode, String warehouseCode,
            UUID productId, UUID warehouseId, BigDecimal quantity) {
        UUID id = ValeryIds.stockMovement(productCode, warehouseCode);
        if (entityManager.find(StockMovementEntity.class, id) != null) {
            return;
        }
        var entity = new StockMovementEntity();
        entity.setId(id);
        entity.setWarehouseId(warehouseId);
        entity.setProductId(productId);
        entity.setMovementType(quantity.signum() < 0
                ? MovementType.ADJUSTMENT
                : MovementType.ENTRY);
        entity.setQuantity(quantity.abs());
        entity.setPreviousStock(BigDecimal.ZERO);
        entity.setNewStock(quantity);
        entity.setReferenceType(ReferenceType.MANUAL);
        entity.setReferenceNumber("VALERY");
        entity.setNotes("Carga inicial de existencias Valery");
        entity.setCreatedAt(CREATED_AT);
        entityManager.persist(entity);
    }
}
