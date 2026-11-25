package com.parsun.parsun_api.inventory.warehouses.management.application.update.use_case;

import com.parsun.parsun_api.shared.application.ports.in.ActiveTogglePermission;
import com.parsun.parsun_api.platform.notifications.application.ports.in.ActivityEvent;

import com.parsun.parsun_api.platform.notifications.application.ports.in.ActivityNotificationRecorder;

import com.parsun.parsun_api.inventory.warehouses.management.application.update.request.UpdateWarehouseRequest;
import com.parsun.parsun_api.inventory.warehouses.management.application.dto.response.WarehouseDetailResponse;
import com.parsun.parsun_api.inventory.warehouses.management.application.create.services.DuplicateWarehouseCodeException;
import com.parsun.parsun_api.inventory.warehouses.management.application.exceptions.WarehouseNotFoundException;
import com.parsun.parsun_api.inventory.warehouses.management.domain.entity.Warehouse;
import com.parsun.parsun_api.inventory.warehouses.management.domain.repository.WarehouseRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import com.parsun.parsun_api.platform.audit.application.ports.in.Audited;

public class UpdateWarehouseUseCase {

    private final WarehouseRepository warehouseRepository;
    private final ActiveTogglePermission access;

    private final ActivityNotificationRecorder activityNotifications;

    public UpdateWarehouseUseCase(WarehouseRepository warehouseRepository,
            ActiveTogglePermission access,
            ActivityNotificationRecorder activityNotifications) {
        this.activityNotifications = activityNotifications != null
                ? activityNotifications
                : ActivityNotificationRecorder.NONE;
        this.warehouseRepository = warehouseRepository;
        this.access = access;
    }

    @Transactional
    @Audited(module = "inventory", entityType = "WAREHOUSE", action = "EDIT")
    public WarehouseDetailResponse execute(UUID id,
            UpdateWarehouseRequest request) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new WarehouseNotFoundException(id));

        if (request.active() != null
                && request.active() != warehouse.isActive()) {
            access.requireActiveToggle();
        }

        warehouse.update(request.name(), request.address(), request.state(),
                request.municipality(), request.postalCode(), request.active());

        Warehouse saved = warehouseRepository.save(warehouse);
        activityNotifications.record(ActivityEvent.of("INVENTORY_WAREHOUSES",
                "EDIT", "WAREHOUSE", saved.getId(),
                saved.getName(), null));
        return WarehouseDetailResponse.from(saved);
    }
}
