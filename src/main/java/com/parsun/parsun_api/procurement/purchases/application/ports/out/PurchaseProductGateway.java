package com.parsun.parsun_api.procurement.purchases.application.ports.out;

import java.util.Optional;
import java.util.UUID;

public interface PurchaseProductGateway {
    Optional<Entry> findById(UUID id);
    record Entry(UUID id, String code, String name) {
    }
}