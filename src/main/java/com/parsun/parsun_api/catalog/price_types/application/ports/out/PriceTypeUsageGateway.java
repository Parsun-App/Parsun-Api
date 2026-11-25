package com.parsun.parsun_api.catalog.price_types.application.ports.out;

import java.util.Set;
import java.util.UUID;

public interface PriceTypeUsageGateway {
    boolean isInUseByProducts(UUID priceTypeId);
    Set<UUID> findIdsInUseByProducts(Set<UUID> priceTypeIds);
    boolean isInUseByCustomers(UUID priceTypeId);
    Set<UUID> findIdsInUseByCustomers(Set<UUID> priceTypeIds);
}
