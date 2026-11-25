package com.parsun.parsun_api.catalog.categories.application.dto.response;

public record CategoryListResponse(String id, String name, String description,
        boolean active, boolean deletable) {
}
