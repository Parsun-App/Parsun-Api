package com.parsun.parsun_api.partners.suppliers.application.update.use_case;

import com.parsun.parsun_api.shared.application.ports.in.ActiveTogglePermission;
import com.parsun.parsun_api.platform.notifications.application.ports.in.ActivityEvent;

import com.parsun.parsun_api.platform.notifications.application.ports.in.ActivityNotificationRecorder;

import com.parsun.parsun_api.partners.suppliers.application.update.request.UpdateSupplierRequest;
import com.parsun.parsun_api.partners.suppliers.application.dto.response.SupplierDetailResponse;
import com.parsun.parsun_api.partners.suppliers.application.ports.out.PurchaseUsageGateway;
import com.parsun.parsun_api.partners.suppliers.application.exceptions.SupplierNotFoundException;
import com.parsun.parsun_api.partners.suppliers.application.mappers.SupplierMapper;
import com.parsun.parsun_api.partners.suppliers.domain.entity.Supplier;
import com.parsun.parsun_api.partners.suppliers.domain.repository.SupplierRepository;
import com.parsun.parsun_api.partners.suppliers.domain.valueobjects.CreditDays;
import com.parsun.parsun_api.partners.suppliers.domain.valueobjects.CreditLimit;
import com.parsun.parsun_api.partners.suppliers.domain.valueobjects.EmailAddress;
import com.parsun.parsun_api.partners.suppliers.domain.valueobjects.LegalName;
import com.parsun.parsun_api.partners.suppliers.domain.valueobjects.PhoneNumber;
import com.parsun.parsun_api.partners.suppliers.domain.valueobjects.Rif;
import com.parsun.parsun_api.partners.suppliers.domain.valueobjects.SupplierAlias;
import com.parsun.parsun_api.partners.suppliers.domain.valueobjects.SupplierId;
import com.parsun.parsun_api.partners.suppliers.domain.valueobjects.SupplierType;
import org.springframework.transaction.annotation.Transactional;
import com.parsun.parsun_api.platform.audit.application.ports.in.Audited;

public class UpdateSupplierUseCase {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;
    private final ActiveTogglePermission access;
    private final PurchaseUsageGateway purchaseUsage;

    private final ActivityNotificationRecorder activityNotifications;

    public UpdateSupplierUseCase(SupplierRepository supplierRepository,
            SupplierMapper supplierMapper, ActiveTogglePermission access,
            PurchaseUsageGateway purchaseUsage,
            ActivityNotificationRecorder activityNotifications) {
        this.activityNotifications = activityNotifications != null
                ? activityNotifications
                : ActivityNotificationRecorder.NONE;
        this.supplierRepository = supplierRepository;
        this.supplierMapper = supplierMapper;
        this.access = access;
        this.purchaseUsage = purchaseUsage;
    }

    @Transactional
    @Audited(module = "partners", entityType = "SUPPLIER", action = "EDIT")
    public SupplierDetailResponse execute(String id,
            UpdateSupplierRequest request) {
        SupplierId supplierId = SupplierId.from(id);

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new SupplierNotFoundException(
                        "Supplier not found with id: " + id));

        supplier.updateLegalName(new LegalName(request.legalName()));
        supplier.updateType(SupplierType.valueOf(request.type()));
        supplier.updateRif(new Rif(request.rif()));
        supplier.updateAlias(new SupplierAlias(request.alias()));
        supplier.updateNotes(request.notes());
        if (request.creditDays() != null) {
            supplier.updateCreditDays(new CreditDays(request.creditDays()));
        }
        if (request.creditLimit() != null) {
            supplier.updateCreditLimit(
                    new CreditLimit(request.creditLimit()));
        }

        if (request.active() != null
                && request.active() != supplier.isActive()) {
            access.requireActiveToggle();
        }

        if (request.active()) {
            supplier.activate();
        } else {
            supplier.deactivate();
        }

        supplier.clearEmails();
        supplier.clearPhones();
        supplier.clearAddresses();

        request.emails()
                .forEach(e -> supplier.addEmail(new EmailAddress(e.email())));
        request.phones()
                .forEach(p -> supplier.addPhone(new PhoneNumber(p.phone())));
        request.addresses()
                .forEach(a -> supplier.addAddress(a.address(), a.billing(),
                        a.active(), a.state(), a.municipality(),
                        a.postalCode()));

        Supplier saved = supplierRepository.save(supplier);
        activityNotifications.record(ActivityEvent.of("CONTACTS_SUPPLIERS",
                "EDIT", "SUPPLIER", saved.getId().getValue(),
                saved.getLegalName().getValue(), null));
        return supplierMapper.toDetailResponse(saved,
                !purchaseUsage.hasPurchases(saved.getId().getValue()));
    }
}
