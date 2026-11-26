package com.parsun.parsun_api.catalog.categories.application.ports.in;

import java.util.Optional;
import java.util.UUID;

public interface ProductCategoryQuery {
    Optional<Entry> findById(UUID id);
    record Entry(UUID id, String name) {
    }
}
