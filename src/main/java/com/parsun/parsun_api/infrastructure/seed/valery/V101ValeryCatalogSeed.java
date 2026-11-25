package com.parsun.parsun_api.infrastructure.seed.valery;

import com.parsun.parsun_api.catalog.categories.infrastructure.persistence.entities.CategoryEntity;
import com.parsun.parsun_api.catalog.price_types.infrastructure.persistence.entities.PriceTypeEntity;
import com.parsun.parsun_api.infrastructure.seed.SeedSource;
import com.parsun.parsun_api.infrastructure.seed.SeedStep;
import com.parsun.parsun_api.infrastructure.seed.valery.ValeryCsv;
import com.parsun.parsun_api.infrastructure.seed.valery.ValeryIds;
import com.parsun.parsun_api.inventory.warehouses.management.infrastructure.persistence.entities.WarehouseEntity;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class V101ValeryCatalogSeed implements SeedStep {

    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2026, 1,
            1, 8, 0);

    private final EntityManager entityManager;

    public V101ValeryCatalogSeed(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public int version() {
        return 101;
    }

    @Override
    public SeedSource source() {
        return SeedSource.VALERY;
    }

    @Override
    public void seed() {
        priceType(ValeryIds.PRICE_TYPE_MINIMO, "Minimo",
                "Precio minimo (detal)");
        priceType(ValeryIds.PRICE_TYPE_MAYOR, "Mayor", "Precio al mayor");
        priceType(ValeryIds.PRICE_TYPE_MAXIMO, "Maximo",
                "Precio maximo de lista");
        priceType(ValeryIds.PRICE_TYPE_OFERTA, "Oferta", "Precio de oferta");
        categories();
        warehouses();
    }

    private void priceType(String key, String name, String description) {
        UUID id = ValeryIds.priceType(key);
        if (entityManager.find(PriceTypeEntity.class, id) != null) {
            return;
        }
        if (existsByName("PriceTypeEntity", name)) {
            return;
        }
        var entity = new PriceTypeEntity();
        entity.setId(id);
        entity.setName(name);
        entity.setDescription(description);
        entity.setActive(true);
        entity.setCreatedAt(CREATED_AT);
        entity.setUpdatedAt(CREATED_AT);
        entityManager.persist(entity);
    }

    private void categories() {
        for (String[] row : ValeryCsv.load("categories")) {
            String code = ValeryCsv.text(row, 0);
            if (code.isEmpty()) {
                continue;
            }
            UUID id = ValeryIds.category(code);
            if (entityManager.find(CategoryEntity.class, id) != null) {
                continue;
            }
            String name = ValeryCsv.trim(ValeryCsv.orDefault(
                    ValeryCsv.text(row, 1), "CATEGORIA " + code), 100);
            if (existsByName("CategoryEntity", name)) {
                continue;
            }
            var entity = new CategoryEntity();
            entity.setId(id);
            entity.setName(name);
            entity.setDescription("Valery departamento " + code);
            entity.setActive(true);
            entity.setCreatedAt(CREATED_AT);
            entity.setUpdatedAt(CREATED_AT);
            entityManager.persist(entity);
        }
    }

    private void warehouses() {
        for (String[] row : ValeryCsv.load("warehouses")) {
            String code = ValeryCsv.trim(ValeryCsv.text(row, 0), 50);
            if (code.isEmpty()) {
                continue;
            }
            UUID id = ValeryIds.warehouse(code);
            if (entityManager.find(WarehouseEntity.class, id) != null) {
                continue;
            }
            var entity = new WarehouseEntity();
            entity.setId(id);
            entity.setCode(code);
            entity.setName(ValeryCsv.trim(ValeryCsv.orDefault(
                    ValeryCsv.text(row, 1), "ALMACEN " + code), 100));
            entity.setAddress("Sin direccion registrada");
            entity.setState("Venezuela");
            entity.setMunicipality("");
            entity.setPostalCode("");
            entity.setActive(true);
            entity.setCreatedAt(CREATED_AT);
            entity.setUpdatedAt(CREATED_AT);
            entityManager.persist(entity);
        }
    }

    private boolean existsByName(String entity, String name) {
        Long count = entityManager.createQuery("select count(e) from " + entity
                + " e where e.name = :name", Long.class)
                .setParameter("name", name).getSingleResult();
        return count != null && count > 0;
    }
}
