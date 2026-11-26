package com.parsun.parsun_api.partners.suppliers.infrastructure.config;

import com.parsun.parsun_api.platform.notifications.application.ports.in.ActivityNotificationRecorder;
import com.parsun.parsun_api.partners.suppliers.application.ports.out.PurchaseUsageGateway;
import com.parsun.parsun_api.partners.suppliers.application.mappers.SupplierMapper;
import com.parsun.parsun_api.partners.suppliers.application.delete.use_case.DeleteSupplierUseCase;
import com.parsun.parsun_api.partners.suppliers.application.queries.use_case.GetSupplierUseCase;
import com.parsun.parsun_api.partners.suppliers.application.queries.use_case.ListSuppliersUseCase;
import com.parsun.parsun_api.partners.suppliers.application.register.use_case.RegisterSupplierUseCase;
import com.parsun.parsun_api.partners.suppliers.application.update.use_case.UpdateSupplierUseCase;
import com.parsun.parsun_api.partners.suppliers.domain.repository.SupplierRepository;
import com.parsun.parsun_api.partners.suppliers.domain.services.SupplierCodeGenerator;
import com.parsun.parsun_api.partners.suppliers.infrastructure.services.SequentialSupplierCodeGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SupplierConfig {

    @Bean
    public SupplierMapper supplierMapper() {
        return new SupplierMapper();
    }

    @Bean
    public SupplierCodeGenerator supplierCodeGenerator(
            SupplierRepository supplierRepository) {
        return new SequentialSupplierCodeGenerator(supplierRepository);
    }

    @Bean
    public RegisterSupplierUseCase registerSupplierUseCase(
            SupplierRepository supplierRepository,
            SupplierCodeGenerator supplierCodeGenerator,
            SupplierMapper supplierMapper,
            ActivityNotificationRecorder activityNotifications) {
        return new RegisterSupplierUseCase(supplierRepository,
                supplierCodeGenerator, supplierMapper, activityNotifications);
    }

    @Bean
    public GetSupplierUseCase getSupplierUseCase(
            SupplierRepository supplierRepository,
            SupplierMapper supplierMapper, PurchaseUsageGateway purchaseUsage) {
        return new GetSupplierUseCase(supplierRepository, supplierMapper,
                purchaseUsage);
    }

    @Bean
    public ListSuppliersUseCase listSuppliersUseCase(
            SupplierRepository supplierRepository,
            SupplierMapper supplierMapper, PurchaseUsageGateway purchaseUsage) {
        return new ListSuppliersUseCase(supplierRepository, supplierMapper,
                purchaseUsage);
    }

    @Bean
    public UpdateSupplierUseCase updateSupplierUseCase(
            SupplierRepository supplierRepository,
            SupplierMapper supplierMapper,
            com.parsun.parsun_api.shared.application.ports.in.ActiveTogglePermission access,
            PurchaseUsageGateway purchaseUsage,
            ActivityNotificationRecorder activityNotifications) {
        return new UpdateSupplierUseCase(supplierRepository, supplierMapper,
                access, purchaseUsage, activityNotifications);
    }

    @Bean
    public DeleteSupplierUseCase deleteSupplierUseCase(
            SupplierRepository supplierRepository,
            PurchaseUsageGateway purchaseUsage,
            ActivityNotificationRecorder activityNotifications) {
        return new DeleteSupplierUseCase(supplierRepository, purchaseUsage, activityNotifications);
    }
}
