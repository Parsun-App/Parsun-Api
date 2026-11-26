package com.parsun.parsun_api.partners.customers.warehouse_access.application.revoke.use_case;

import com.parsun.parsun_api.platform.notifications.application.ports.in.ActivityEvent;

import com.parsun.parsun_api.platform.notifications.application.ports.in.ActivityNotificationRecorder;

import com.parsun.parsun_api.partners.customers.warehouse_access.application.exceptions.CustomerWarehouseAccessNotFoundException;
import com.parsun.parsun_api.partners.customers.warehouse_access.domain.entity.CustomerWarehouseAccess;
import com.parsun.parsun_api.partners.customers.warehouse_access.domain.repository.CustomerWarehouseAccessRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import com.parsun.parsun_api.platform.audit.application.ports.in.Audited;

public class RevokeWarehouseAccessUseCase {

    private final CustomerWarehouseAccessRepository accessRepository;

    private final ActivityNotificationRecorder activityNotifications;

    public RevokeWarehouseAccessUseCase(
            CustomerWarehouseAccessRepository accessRepository,
            ActivityNotificationRecorder activityNotifications) {
        this.activityNotifications = activityNotifications != null
                ? activityNotifications
                : ActivityNotificationRecorder.NONE;
        this.accessRepository = accessRepository;
    }

    @Transactional
    @Audited(module = "partners", entityType = "CUSTOMER", action = "EDIT")
    public void execute(UUID customerId, UUID warehouseId) {
        CustomerWarehouseAccess access = accessRepository
                .findByCustomerIdAndWarehouseId(customerId, warehouseId)
                .orElseThrow(() -> new CustomerWarehouseAccessNotFoundException(
                        customerId, warehouseId));

        activityNotifications.record(ActivityEvent.of("CONTACTS_CUSTOMERS",
                "EDIT", "CUSTOMER", customerId,
                customerId.toString(), null));
        accessRepository.delete(access);
    }
}
