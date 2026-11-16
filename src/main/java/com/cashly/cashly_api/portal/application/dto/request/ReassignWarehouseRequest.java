package com.cashly.cashly_api.portal.application.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ReassignWarehouseRequest(
        @NotNull(message = "Warehouse is required") UUID warehouseId) {
}
