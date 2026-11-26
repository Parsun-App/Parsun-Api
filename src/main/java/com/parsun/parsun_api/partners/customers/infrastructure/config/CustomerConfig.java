package com.parsun.parsun_api.partners.customers.infrastructure.config;

import com.parsun.parsun_api.partners.customers.profile.application.ports.out.PortalPasswordEncoder;
import com.parsun.parsun_api.platform.notifications.application.ports.in.ActivityNotificationRecorder;
import com.parsun.parsun_api.partners.customers.profile.application.mappers.CustomerMapper;
import com.parsun.parsun_api.partners.customers.credit.application.ports.out.CreditValidationGateway;
import com.parsun.parsun_api.partners.customers.balance.application.ports.out.CustomerActorGateway;
import com.parsun.parsun_api.partners.customers.balance.application.ports.out.CustomerCashGateway;
import com.parsun.parsun_api.partners.customers.balance.application.add.use_case.AddToCustomerBalanceUseCase;
import com.parsun.parsun_api.partners.customers.balance.application.consume.use_case.ConsumeCustomerBalanceUseCase;
import com.parsun.parsun_api.partners.customers.balance.application.advance.use_case.CreateCustomerAdvanceUseCase;
import com.parsun.parsun_api.partners.customers.profile.application.delete.use_case.DeleteCustomerUseCase;
import com.parsun.parsun_api.partners.customers.balance.application.get.use_case.GetCustomerBalanceUseCase;
import com.parsun.parsun_api.partners.customers.profile.application.get.use_case.GetCustomerUseCase;
import com.parsun.parsun_api.partners.customers.warehouse_access.application.grant.use_case.GrantWarehouseAccessUseCase;
import com.parsun.parsun_api.partners.customers.warehouse_access.application.list.use_case.ListCustomerWarehousesUseCase;
import com.parsun.parsun_api.partners.customers.profile.application.list.use_case.ListCustomersUseCase;
import com.parsun.parsun_api.partners.customers.profile.application.portal.use_case.ChangeCustomerPortalPasswordUseCase;
import com.parsun.parsun_api.partners.customers.balance.application.refund.use_case.RefundCustomerBalanceUseCase;
import com.parsun.parsun_api.partners.customers.profile.application.register.use_case.RegisterCustomerUseCase;
import com.parsun.parsun_api.partners.customers.warehouse_access.application.revoke.use_case.RevokeWarehouseAccessUseCase;
import com.parsun.parsun_api.partners.customers.profile.application.update.use_case.UpdateCustomerUseCase;
import com.parsun.parsun_api.partners.customers.credit.application.status.use_case.GetCustomerCreditStatusUseCase;
import com.parsun.parsun_api.partners.customers.credit.application.overdue.use_case.GetOverdueInvoicesUseCase;
import com.parsun.parsun_api.partners.customers.balance.domain.repository.CustomerBalanceRepository;
import com.parsun.parsun_api.partners.customers.profile.domain.repository.CustomerRepository;
import com.parsun.parsun_api.partners.customers.warehouse_access.domain.repository.CustomerWarehouseAccessRepository;
import com.parsun.parsun_api.partners.customers.profile.domain.services.CustomerCodeGenerator;
import com.parsun.parsun_api.partners.customers.profile.infrastructure.services.SequentialCustomerCodeGenerator;
import com.parsun.parsun_api.partners.customers.warehouse_access.application.ports.out.WarehouseDirectoryGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomerConfig {

    @Bean
    public CustomerMapper customerMapper() {
        return new CustomerMapper();
    }

    @Bean
    public CustomerCodeGenerator customerCodeGenerator(
            CustomerRepository customerRepository) {
        return new SequentialCustomerCodeGenerator(customerRepository);
    }

    @Bean
    public RegisterCustomerUseCase registerCustomerUseCase(
            CustomerRepository customerRepository,
            CustomerCodeGenerator customerCodeGenerator,
            CustomerMapper customerMapper,
            CustomerWarehouseAccessRepository warehouseAccessRepository,
            WarehouseDirectoryGateway warehouseDirectory,
            PortalPasswordEncoder passwordEncoder,
            ActivityNotificationRecorder activityNotifications) {
        return new RegisterCustomerUseCase(customerRepository,
                customerCodeGenerator, customerMapper,
                warehouseAccessRepository, warehouseDirectory, passwordEncoder,
                activityNotifications);
    }

    @Bean
    public ChangeCustomerPortalPasswordUseCase changeCustomerPortalPasswordUseCase(
            CustomerRepository customerRepository,
            PortalPasswordEncoder passwordEncoder) {
        return new ChangeCustomerPortalPasswordUseCase(customerRepository,
                passwordEncoder);
    }

    @Bean
    public GetCustomerUseCase getCustomerUseCase(
            CustomerRepository customerRepository,
            CustomerMapper customerMapper) {
        return new GetCustomerUseCase(customerRepository, customerMapper);
    }

    @Bean
    public ListCustomersUseCase listCustomersUseCase(
            CustomerRepository customerRepository,
            CustomerMapper customerMapper) {
        return new ListCustomersUseCase(customerRepository, customerMapper);
    }

    @Bean
    public UpdateCustomerUseCase updateCustomerUseCase(
            CustomerRepository customerRepository,
            CustomerMapper customerMapper,
            PortalPasswordEncoder passwordEncoder,
            com.parsun.parsun_api.shared.application.ports.in.ActiveTogglePermission access,
            ActivityNotificationRecorder activityNotifications) {
        return new UpdateCustomerUseCase(customerRepository, customerMapper,
                passwordEncoder, access, activityNotifications);
    }

    @Bean
    public DeleteCustomerUseCase deleteCustomerUseCase(
            CustomerRepository customerRepository,
            ActivityNotificationRecorder activityNotifications) {
        return new DeleteCustomerUseCase(customerRepository, activityNotifications);
    }

    @Bean
    public GrantWarehouseAccessUseCase grantWarehouseAccessUseCase(
            CustomerWarehouseAccessRepository accessRepository,
            CustomerRepository customerRepository,
            WarehouseDirectoryGateway warehouseDirectory,
            ActivityNotificationRecorder activityNotifications) {
        return new GrantWarehouseAccessUseCase(accessRepository,
                customerRepository, warehouseDirectory, activityNotifications);
    }

    @Bean
    public RevokeWarehouseAccessUseCase revokeWarehouseAccessUseCase(
            CustomerWarehouseAccessRepository accessRepository,
            ActivityNotificationRecorder activityNotifications) {
        return new RevokeWarehouseAccessUseCase(accessRepository, activityNotifications);
    }

    @Bean
    public ListCustomerWarehousesUseCase listCustomerWarehousesUseCase(
            CustomerWarehouseAccessRepository accessRepository,
            CustomerRepository customerRepository,
            WarehouseDirectoryGateway warehouseDirectory) {
        return new ListCustomerWarehousesUseCase(accessRepository,
                customerRepository, warehouseDirectory);
    }

    @Bean
    public GetCustomerBalanceUseCase getCustomerBalanceUseCase(
            CustomerBalanceRepository customerBalanceRepository) {
        return new GetCustomerBalanceUseCase(customerBalanceRepository);
    }

    @Bean
    public AddToCustomerBalanceUseCase addToCustomerBalanceUseCase(
            CustomerBalanceRepository customerBalanceRepository,
            ActivityNotificationRecorder activityNotifications) {
        return new AddToCustomerBalanceUseCase(customerBalanceRepository, activityNotifications);
    }

    @Bean
    public ConsumeCustomerBalanceUseCase consumeCustomerBalanceUseCase(
            CustomerBalanceRepository customerBalanceRepository,
            ActivityNotificationRecorder activityNotifications) {
        return new ConsumeCustomerBalanceUseCase(customerBalanceRepository, activityNotifications);
    }

    @Bean
    public CreateCustomerAdvanceUseCase createCustomerAdvanceUseCase(
            AddToCustomerBalanceUseCase addToCustomerBalanceUseCase,
            CustomerCashGateway cashGateway,
            CustomerActorGateway actorGateway,
            ActivityNotificationRecorder activityNotifications) {
        return new CreateCustomerAdvanceUseCase(addToCustomerBalanceUseCase,
                cashGateway, actorGateway, activityNotifications);
    }

    @Bean
    public RefundCustomerBalanceUseCase refundCustomerBalanceUseCase(
            ConsumeCustomerBalanceUseCase consumeCustomerBalanceUseCase,
            CustomerCashGateway cashGateway,
            CustomerActorGateway actorGateway,
            ActivityNotificationRecorder activityNotifications) {
        return new RefundCustomerBalanceUseCase(consumeCustomerBalanceUseCase,
                cashGateway, actorGateway, activityNotifications);
    }

    @Bean
    public GetCustomerCreditStatusUseCase getCustomerCreditStatusUseCase(
            CreditValidationGateway creditValidationGateway) {
        return new GetCustomerCreditStatusUseCase(creditValidationGateway);
    }

    @Bean
    public GetOverdueInvoicesUseCase getOverdueInvoicesUseCase(
            CreditValidationGateway creditValidationGateway) {
        return new GetOverdueInvoicesUseCase(creditValidationGateway);
    }
}
