package com.parsun.parsun_api.sales.invoices.returns.infrastructure.integration;
import com.parsun.parsun_api.sales.invoices.returns.application.ports.out.ReturnInventoryGateway;
import com.parsun.parsun_api.sales.invoices.returns.application.ports.out.ReturnInvoiceAdjustmentGateway;
import com.parsun.parsun_api.sales.invoices.returns.application.ports.out.ReturnInvoiceQueryGateway;
import com.parsun.parsun_api.sales.invoices.returns.application.ports.out.ReturnProjectionQueryGateway;
import com.parsun.parsun_api.sales.invoices.returns.application.mappers.ReturnMapper;
import com.parsun.parsun_api.sales.invoices.returns.application.create.use_case.CreateReturnUseCase;
import com.parsun.parsun_api.sales.invoices.returns.application.delete.use_case.DeleteReturnUseCase;
import com.parsun.parsun_api.sales.invoices.returns.domain.repository.ReturnRepository;
import com.parsun.parsun_api.sales.invoices.returns.infrastructure.config.ReturnConfig;
import com.parsun.parsun_api.sales.invoices.invoices.application.queries.services.RepositoryReturnInvoiceQuery;
import com.parsun.parsun_api.sales.invoices.invoices.infrastructure.config.ReturnInvoiceQueryConfig;
import com.parsun.parsun_api.sales.invoices.invoices.domain.repository.InvoiceRepository;
import com.parsun.parsun_api.sales.invoices.credit_notes.domain.repository.CreditNoteRepository;
import com.parsun.parsun_api.sales.invoices.credit_notes.infrastructure.config.CreditNoteLedgerQueryConfig;
import com.parsun.parsun_api.sales.invoices.debit_notes.domain.repository.DebitNoteRepository;
import com.parsun.parsun_api.sales.invoices.debit_notes.infrastructure.config.DebitNoteLedgerQueryConfig;
import com.parsun.parsun_api.finance.treasury.payments.collection.domain.repository.PaymentRepository;
import com.parsun.parsun_api.finance.treasury.payments.collection.domain.repository.PaymentAllocationRepository;
import com.parsun.parsun_api.finance.treasury.payments.collection.domain.services.PaymentAllocationService;
import com.parsun.parsun_api.finance.treasury.payments.collection.infrastructure.config.ReturnPaymentOperationsConfig;
import com.parsun.parsun_api.partners.customers.balance.application.add.use_case.AddToCustomerBalanceUseCase;
import com.parsun.parsun_api.partners.customers.balance.application.consume.use_case.ConsumeCustomerBalanceUseCase;
import com.parsun.parsun_api.partners.customers.balance.infrastructure.config.ReturnBalanceMutationConfig;
import com.parsun.parsun_api.finance.treasury.cash_register.sessions.application.movements.services.CashMovementService;
import com.parsun.parsun_api.finance.treasury.cash_register.sessions.infrastructure.config.ReturnCashRecordingConfig;
import com.parsun.parsun_api.inventory.warehouses.management.domain.repository.WarehouseRepository;
import com.parsun.parsun_api.inventory.warehouses.management.infrastructure.config.WarehouseDirectoryConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
class ReturnFinancialWiringTest {
    @Test
    void should_wire_return_owned_gateways_to_neutral_public_providers() {
        try (var context = new AnnotationConfigApplicationContext()) {
            context.registerBean(com.parsun.parsun_api.platform.notifications.application.ports.in.ActivityNotificationRecorder.class,
                    () -> mock(com.parsun.parsun_api.platform.notifications.application.ports.in.ActivityNotificationRecorder.class));
            context.registerBean(ReturnRepository.class,
                    () -> mock(ReturnRepository.class));
            context.registerBean(ReturnInvoiceAdjustmentGateway.class,
                    () -> mock(ReturnInvoiceAdjustmentGateway.class));
            context.registerBean(ReturnInventoryGateway.class,
                    () -> mock(ReturnInventoryGateway.class));
            context.registerBean(InvoiceRepository.class,
                    () -> mock(InvoiceRepository.class));
            context.registerBean(CreditNoteRepository.class,
                    () -> mock(CreditNoteRepository.class));
            context.registerBean(DebitNoteRepository.class,
                    () -> mock(DebitNoteRepository.class));
            context.registerBean(PaymentRepository.class,
                    () -> mock(PaymentRepository.class));
            context.registerBean(PaymentAllocationRepository.class,
                    () -> mock(PaymentAllocationRepository.class));
            context.registerBean(PaymentAllocationService.class,
                    () -> mock(PaymentAllocationService.class));
            context.registerBean(AddToCustomerBalanceUseCase.class,
                    () -> mock(AddToCustomerBalanceUseCase.class));
            context.registerBean(ConsumeCustomerBalanceUseCase.class,
                    () -> mock(ConsumeCustomerBalanceUseCase.class));
            context.registerBean(CashMovementService.class,
                    () -> mock(CashMovementService.class));
            context.registerBean(WarehouseRepository.class,
                    () -> mock(WarehouseRepository.class));
            context.register(ReturnConfig.class, ReturnMapper.class,
                    com.parsun.parsun_api.sales.invoices.returns.application.services.ReturnResponseAssembler.class,
                    ReturnInvoiceQueryConfig.class,
                    CreditNoteLedgerQueryConfig.class,
                    DebitNoteLedgerQueryConfig.class,
                    ReturnPaymentOperationsConfig.class,
                    ReturnBalanceMutationConfig.class,
                    ReturnCashRecordingConfig.class,
                    WarehouseDirectoryConfig.class,
                    LocalReturnInvoiceQueryGateway.class,
                    LocalReturnPaymentSettlementGateway.class,
                    LocalReturnBalanceGateway.class,
                    LocalReturnCashGateway.class,
                    LocalReturnProjectionQueryGateway.class);
            context.refresh();
            assertThat(context.getBean(CreateReturnUseCase.class)).isNotNull();
            assertThat(context.getBean(DeleteReturnUseCase.class)).isNotNull();
            assertThat(context.getBean(ReturnMapper.class)).isNotNull();
            UUID id = UUID.randomUUID();
            assertThat(context.getBean(ReturnProjectionQueryGateway.class)
                    .findInvoice(id)).isEmpty();
            assertThat(context.getBean(ReturnProjectionQueryGateway.class)
                    .findWarehouseName(id)).isEmpty();
            assertThatThrownBy(() -> context
                    .getBean(ReturnInvoiceQueryGateway.class).requireById(id))
                    .isInstanceOf(
                            com.parsun.parsun_api.sales.invoices.invoices.domain.exceptions.InvoiceNotFoundException.class);
        }
    }
}
