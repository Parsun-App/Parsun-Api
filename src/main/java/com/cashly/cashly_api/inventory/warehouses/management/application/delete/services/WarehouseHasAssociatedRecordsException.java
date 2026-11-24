package com.cashly.cashly_api.inventory.warehouses.management.application.delete.services;

import java.util.UUID;

public class WarehouseHasAssociatedRecordsException extends RuntimeException {
    public WarehouseHasAssociatedRecordsException(UUID id) {
        super("Warehouse " + id + " has associated records and cannot be deleted");
    }
}
