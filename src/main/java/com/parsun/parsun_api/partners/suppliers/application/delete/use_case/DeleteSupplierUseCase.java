package com.parsun.parsun_api.partners.suppliers.application.delete.use_case;

import com.parsun.parsun_api.platform.notifications.application.ports.in.ActivityEvent;

import com.parsun.parsun_api.platform.notifications.application.ports.in.ActivityNotificationRecorder;

import com.parsun.parsun_api.partners.suppliers.application.ports.out.PurchaseUsageGateway;
import com.parsun.parsun_api.partners.suppliers.application.exceptions.SupplierHasAssociatedRecordsException;
import com.parsun.parsun_api.partners.suppliers.application.exceptions.SupplierNotFoundException;
import com.parsun.parsun_api.partners.suppliers.domain.repository.SupplierRepository;
import com.parsun.parsun_api.partners.suppliers.domain.valueobjects.SupplierId;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import com.parsun.parsun_api.platform.audit.application.ports.in.Audited;

public class DeleteSupplierUseCase {

    private final SupplierRepository supplierRepository;
    private final PurchaseUsageGateway purchaseUsage;

    private final ActivityNotificationRecorder activityNotifications;

    public DeleteSupplierUseCase(SupplierRepository supplierRepository,
            PurchaseUsageGateway purchaseUsage,
            ActivityNotificationRecorder activityNotifications) {
        this.activityNotifications = activityNotifications != null
                ? activityNotifications
                : ActivityNotificationRecorder.NONE;
        this.supplierRepository = supplierRepository;
        this.purchaseUsage = purchaseUsage;
    }

    @Transactional
    @Audited(module = "partners", entityType = "SUPPLIER", action = "DELETE")
    public void execute(String id) {
        SupplierId supplierId = SupplierId.from(id);
        if (!supplierRepository.existsById(supplierId)) {
            throw new SupplierNotFoundException(
                    "Supplier not found with id: " + id);
        }
        if (purchaseUsage
                .hasPurchases(UUID.fromString(supplierId.toString()))) {
            throw new SupplierHasAssociatedRecordsException(id);
        }
        activityNotifications.record(ActivityEvent.of("CONTACTS_SUPPLIERS",
                "DELETE", "SUPPLIER", supplierId.getValue(),
                supplierId.toString(), null));
        supplierRepository.delete(supplierId);
    }
}
