package com.parsun.parsun_api.infrastructure.seed.valery;

import com.parsun.parsun_api.catalog.categories.infrastructure.persistence.entities.CategoryEntity;
import com.parsun.parsun_api.catalog.products.infrastructure.persistence.entities.ProductEntity;
import com.parsun.parsun_api.infrastructure.seed.SeedSource;
import com.parsun.parsun_api.infrastructure.seed.SeedStep;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class V102ValeryProductsSeed implements SeedStep {

    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2026, 1,
            1, 8, 0);

    private final EntityManager entityManager;

    public V102ValeryProductsSeed(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public int version() {
        return 102;
    }

    @Override
    public SeedSource source() {
        return SeedSource.VALERY;
    }

    @Override
    public void seed() {
        for (String[] row : ValeryCsv.load("products")) {
            String code = ValeryCsv.trim(ValeryCsv.text(row, 0), 50);
            if (code.isEmpty()) {
                continue;
            }
            UUID id = ValeryIds.product(code);
            if (entityManager.find(ProductEntity.class, id) != null) {
                continue;
            }
            String type = ValeryCsv.text(row, 1);
            var product = new ProductEntity();
            product.setId(id);
            product.setCode(code);
            product.setName(ValeryCsv.trim(ValeryCsv.orDefault(
                    ValeryCsv.text(row, 2), code), 200));
            product.setDescription(ValeryCsv.trim(ValeryCsv.text(row, 6), 500));
            product.setCategoryId(categoryId(ValeryCsv.text(row, 3)));
            product.setComposite("C".equalsIgnoreCase(type));
            product.setActive(!"I".equalsIgnoreCase(ValeryCsv.text(row, 4)));
            product.setPurchasePrice(ValeryCsv.money(row, 5));
            product.setCreatedAt(CREATED_AT);
            product.setUpdatedAt(CREATED_AT);
            entityManager.persist(product);
        }
    }

    private UUID categoryId(String code) {
        UUID id = ValeryIds.category(code == null ? "" : code.trim());
        if (code != null && !code.isBlank()
                && entityManager.find(CategoryEntity.class, id) != null) {
            return id;
        }
        UUID generic = ValeryIds.category("00");
        if (entityManager.find(CategoryEntity.class, generic) != null) {
            return generic;
        }
        return null;
    }
}
