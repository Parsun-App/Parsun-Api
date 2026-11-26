package com.parsun.parsun_api.finance.treasury.payments.collection.application.approve.use_case;

import com.parsun.parsun_api.finance.treasury.payments.collection.application.ports.out.CashRegisterGateway;
import com.parsun.parsun_api.finance.treasury.payments.collection.application.ports.out.PaymentInvoiceGateway;
import com.parsun.parsun_api.platform.notifications.application.ports.in.ActivityNotificationRecorder;
import com.parsun.parsun_api.sales.invoices.invoices.domain.entity.Invoice;
import com.parsun.parsun_api.sales.invoices.invoices.domain.entity.InvoiceLine;
import com.parsun.parsun_api.sales.invoices.invoices.domain.valueobjects.InvoiceId;
import com.parsun.parsun_api.sales.invoices.invoices.domain.valueobjects.InvoiceNumber;
import com.parsun.parsun_api.sales.invoices.invoices.domain.valueobjects.InvoiceStatus;
import com.parsun.parsun_api.sales.invoices.invoices.domain.valueobjects.PaymentType;
import com.parsun.parsun_api.finance.treasury.payments.collection.application.approve.request.ApprovePaymentRequest;
import com.parsun.parsun_api.finance.treasury.payments.collection.application.dto.response.PaymentResponse;
import com.parsun.parsun_api.finance.treasury.payments.collection.domain.entity.Payment;
import com.parsun.parsun_api.finance.treasury.payments.collection.domain.entity.PaymentAllocation;
import com.parsun.parsun_api.finance.treasury.payments.collection.domain.repository.PaymentAllocationRepository;
import com.parsun.parsun_api.finance.treasury.payments.collection.domain.repository.PaymentRepository;
import com.parsun.parsun_api.finance.treasury.payments.collection.domain.services.PaymentAllocationService;
import com.parsun.parsun_api.finance.treasury.payments.collection.domain.valueobjects.PaymentId;
import com.parsun.parsun_api.finance.treasury.payments.collection.domain.valueobjects.PaymentNumber;
import com.parsun.parsun_api.finance.treasury.payments.collection.domain.valueobjects.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApprovePaymentUseCaseTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private PaymentAllocationRepository allocationRepository;
    @Mock
    private PaymentInvoiceGateway invoiceGateway;
    @Mock
    private ActivityNotificationRecorder activityNotifications;
    @Mock
    private CashRegisterGateway cashRegisterGateway;

    private ApprovePaymentUseCase useCase;
    private final UUID userId = UUID.randomUUID();
    private final UUID customerId = UUID.randomUUID();
    private final UUID paymentMethodId = UUID.randomUUID();
    private final UUID invoiceId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        PaymentAllocationService paymentAllocationService = new PaymentAllocationService(
                paymentRepository, allocationRepository);
        useCase = new ApprovePaymentUseCase(paymentRepository,
                allocationRepository, invoiceGateway,
                cashRegisterGateway, paymentAllocationService,
                activityNotifications);
    }

    private Payment pendingPayment(BigDecimal amount) {
        return Payment.create(PaymentNumber.of(2026, 1), customerId, amount,
                paymentMethodId, "REF-001", LocalDate.now(), null, userId,
                "Admin", PaymentStatus.PENDING, invoiceId, null);
    }

    private Invoice unpaidInvoice(UUID id, BigDecimal lineQty,
            BigDecimal unitPrice) {
        return Invoice.reconstitute(InvoiceId.of(id), InvoiceNumber.of(2026, 1),
                customerId, "Test Customer", "J-12345678-9", UUID.randomUUID(),
                "Creator", null, null, InvoiceStatus.PENDING_PAYMENT,
                PaymentType.CREDIT, null, BigDecimal.ZERO, null, null, null,
                List.of(InvoiceLine.create(UUID.randomUUID(), "Product", "P001",
                        lineQty, unitPrice, List.of())),
                LocalDate.now().atStartOfDay(), LocalDate.now().atStartOfDay());
    }

    private List<PaymentInvoiceGateway.InvoiceSnapshot> snapshots(
            Invoice... invoices) {
        return java.util.Arrays.stream(invoices)
                .map(invoice -> new PaymentInvoiceGateway.InvoiceSnapshot(
                        invoice.getId().value(), invoice.getCustomerId(),
                        invoice.getInvoiceNumber().value(),
                        invoice.calculateTotal(BigDecimal.ZERO,
                                BigDecimal.ZERO)))
                .toList();
    }

    private void stubSave() {
        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void should_NotApprovePendingPayment_When_RegisterIsClosed() {
        Payment payment = pendingPayment(new BigDecimal("125.00"));
        when(paymentRepository.findById(payment.getId()))
                .thenReturn(Optional.of(payment));
        doThrow(new IllegalStateException(
                "Debe abrir una caja antes de registrar una operación monetaria"))
                .when(cashRegisterGateway).requireOpenRegister();

        assertThrows(IllegalStateException.class,
                () -> useCase.execute(payment.getId().value(),
                        new ApprovePaymentRequest("Verificado"), userId,
                        "Admin"));

        assertEquals(PaymentStatus.PENDING, payment.getStatus());
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void should_ApplyFullAmountToTargetInvoice_When_PaymentIsApproved() {
        Payment payment = pendingPayment(new BigDecimal("500.00"));
        when(paymentRepository.findById(payment.getId()))
                .thenReturn(Optional.of(payment));
        when(invoiceGateway.findById(invoiceId))
                .thenReturn(Optional.of(new PaymentInvoiceGateway.InvoiceSnapshot(
                        invoiceId, customerId, "INV-2026-00001",
                        new BigDecimal("500.00"))));
        stubSave();

        PaymentResponse response = useCase.execute(payment.getId().value(),
                new ApprovePaymentRequest("Verificado"), userId, "Admin");

        assertEquals(new BigDecimal("500.00"), response.appliedAmount());
        assertEquals(invoiceId, response.targetInvoiceId());
        verify(invoiceGateway).recalculateStatus(invoiceId);
        verify(allocationRepository).save(any(PaymentAllocation.class));
    }

    @Test
    void should_Throw_When_TargetInvoiceDoesNotExist() {
        Payment payment = pendingPayment(new BigDecimal("500.00"));
        when(paymentRepository.findById(payment.getId()))
                .thenReturn(Optional.of(payment));
        when(invoiceGateway.findById(invoiceId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(payment.getId().value(),
                        new ApprovePaymentRequest("Verificado"), userId,
                        "Admin"));

        assertEquals("Invoice not found: " + invoiceId,
                exception.getMessage());
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void should_RecordIncome_When_PendingPaymentIsApproved() {
        Payment payment = pendingPayment(new BigDecimal("125.00"));
        when(paymentRepository.findById(payment.getId()))
                .thenReturn(Optional.of(payment));
        when(invoiceGateway.findById(invoiceId))
                .thenReturn(Optional.of(new PaymentInvoiceGateway.InvoiceSnapshot(
                        invoiceId, customerId, "INV-2026-00001",
                        new BigDecimal("125.00"))));
        stubSave();

        useCase.execute(payment.getId().value(),
                new ApprovePaymentRequest("Verificado"), userId, "Admin");

        verify(cashRegisterGateway)
                .recordMovement(new CashRegisterGateway.Movement(
                        CashRegisterGateway.Direction.IN,
                        CashRegisterGateway.Type.PAYMENT,
                        new CashRegisterGateway.PaymentEntry(
                                new BigDecimal("125.00"), paymentMethodId, null,
                                payment.getId().value(), userId, "Admin")));
        verify(activityNotifications).record(org.mockito.ArgumentMatchers.any());
    }
}
