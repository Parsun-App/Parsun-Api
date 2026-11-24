package com.cashly.cashly_api.catalog.price_types.infrastructure.integration;

import com.cashly.cashly_api.catalog.price_types.application.ports.out.PriceTypeUsageGateway;
import com.cashly.cashly_api.catalog.products.application.ports.in.PriceTypeProductUsageApi;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
public class ProductPriceTypeUsageAdapter implements PriceTypeUsageGateway {
    private final PriceTypeProductUsageApi usageApi;
    private final EntityManager entityManager;

    public ProductPriceTypeUsageAdapter(PriceTypeProductUsageApi usageApi,
            EntityManager entityManager) {
        this.usageApi = usageApi;
        this.entityManager = entityManager;
    }

    @Override
    public boolean isInUseByProducts(UUID priceTypeId) {
        return usageApi.isInUseByProducts(priceTypeId);
    }

    @Override
    public Set<UUID> findIdsInUseByProducts(Set<UUID> priceTypeIds) {
        return usageApi.findIdsInUseByProducts(priceTypeIds);
    }

    @Override
    public boolean isInUseByCustomers(UUID priceTypeId) {
        return findIdsInUseByCustomers(Set.of(priceTypeId))
                .contains(priceTypeId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<UUID> findIdsInUseByCustomers(Set<UUID> priceTypeIds) {
        if (priceTypeIds == null || priceTypeIds.isEmpty()) {
            return Set.of();
        }
        var rows = entityManager.createNativeQuery("""
                SELECT BIN_TO_UUID(price_type_id) FROM customers
                WHERE price_type_id IN (:ids)
                """).setParameter("ids", priceTypeIds).getResultList();
        Set<UUID> ids = new HashSet<>();
        for (Object row : rows) {
            ids.add(UUID.fromString(row.toString()));
        }
        return Set.copyOf(ids);
    }
}
