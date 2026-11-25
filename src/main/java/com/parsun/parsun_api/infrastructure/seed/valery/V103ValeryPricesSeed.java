package com.parsun.parsun_api.infrastructure.seed.valery;

import com.parsun.parsun_api.catalog.products.infrastructure.persistence.entities.ProductEntity;
import com.parsun.parsun_api.catalog.products.infrastructure.persistence.entities.ProductPriceEntity;
import com.parsun.parsun_api.infrastructure.seed.SeedSource;
import com.parsun.parsun_api.infrastructure.seed.SeedStep;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class V103ValeryPricesSeed implements SeedStep {

    private final EntityManager entityManager;

    public V103ValeryPricesSeed(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public int version() {
        return 103;
    }

    @Override
    public SeedSource source() {
        return SeedSource.VALERY;
    }

    @Override
    public void seed() {
        for (String[] row : ValeryCsv.load("prices")) {
            String code = ValeryCsv.text(row, 0);
            if (code.isEmpty()) {
                continue;
            }
            UUID productId = ValeryIds.product(code);
            if (entityManager.find(ProductEntity.class, productId) == null) {
                continue;
            }
            Map<String, BigDecimal> values = new LinkedHashMap<>();
            values.put(ValeryIds.PRICE_TYPE_MINIMO, ValeryCsv.money(row, 1));
            values.put(ValeryIds.PRICE_TYPE_MAYOR, ValeryCsv.money(row, 2));
            values.put(ValeryIds.PRICE_TYPE_MAXIMO, ValeryCsv.money(row, 3));
            values.put(ValeryIds.PRICE_TYPE_OFERTA, ValeryCsv.money(row, 4));
            values.forEach((typeKey, price) -> persist(code, productId, typeKey,
                    price));
        }
    }

    private void persist(String code, UUID productId, String typeKey,
            BigDecimal price) {
        if (price == null || price.signum() <= 0) {
            return;
        }
        UUID id = ValeryIds.productPrice(code, typeKey);
        if (entityManager.find(ProductPriceEntity.class, id) != null) {
            return;
        }
        var entity = new ProductPriceEntity();
        entity.setId(id);
        entity.setProduct(entityManager.getReference(ProductEntity.class,
                productId));
        entity.setPriceTypeId(ValeryIds.priceType(typeKey));
        entity.setPrice(price);
        entityManager.persist(entity);
    }
}
