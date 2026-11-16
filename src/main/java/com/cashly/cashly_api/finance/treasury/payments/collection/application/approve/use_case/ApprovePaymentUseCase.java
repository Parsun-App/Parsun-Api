package com.cashly.cashly_api.finance.treasury.payments.collection.application.approve.use_case;

import com.cashly.cashly_api.platform.notifications.application.ports.in.ActivityEvent;

import com.cashly.cashly_api.platform.notifications.application.ports.in.ActivityNotificationRecorder;

import com.cashly.cashly_api.platform.notifications.application.ports.in.CustomerNotificationRecorder;
import com.cashly.cashly_api.finance.treasury.payments.collection.application.ports.out.CashRegisterGateway;
import com.cashly.cashly_api.finance.treasury.payments.collection.application.ports.out.PaymentInvoiceGateway;
import com.cashly.cashly_api.finance.treasury.payments.collection.application.approve.request.ApprovePaymentRequest;
import com.cashly.cashly_api.finance.treasury.payments.collection.application.dto.response.PaymentResponse;
import com.cashly.cashly_api.finance.treasury.payments.collection.application.mappers.PaymentMapper;
import com.cashly.cashly_api.finance.treasury.payments.collection.domain.entity.Payment;
import com.cashly.cashly_api.finance.treasury.payments.collection.domain.repository.PaymentAllocationRepository;
import com.cashly.cashly_api.finance.treasury.payments.collection.domain.repository.PaymentRepository;
import com.cashly.cashly_api.finance.treasury.payments.collection.domain.entity.PaymentAllocation;
import com.cashly.cashly_api.finance.treasury.payments.collection.domain.services.PaymentAllocationService;
import com.cashly.cashly_api.finance.treasury.payments.collection.domain.valueobjects.InvoiceRemainingBalance;
import com.cashly.cashly_api.finance.treasury.payments.collection.domain.valueobjects.PaymentId;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import com.cashly.cashly_api.platform.audit.application.ports.in.Audited;

public class ApprovePaymentUseCase {

    private final PaymentRepository paymentRepository;
    private final PaymentAllocationRepository allocationRepository;
    private final PaymentInvoiceGateway invoices;
    private final CashRegisterGateway cashRegister;
    private final PaymentAllocationService paymentAllocationService;
    private final CustomerNotificationRecorder notificationService;

    private final ActivityNotificationRecorder activityNotifications;

    public ApprovePaymentUseCase(PaymentRepository paymentRepository,
            PaymentAllocationRepository allocationRepository,
            PaymentInvoiceGateway invoices,
            CashRegisterGateway cashRegister,
            PaymentAllocationService paymentAllocationService,
            CustomerNotificationRecorder notificationService,
            ActivityNotificationRecorder activityNotifications) {
        this.activityNotifications = activityNotifications != null
                ? activityNotifications
                : ActivityNotificationRecorder.NONE;
        this.paymentRepository = paymentRepository;
        this.allocationRepository = allocationRepository;
        this.invoices = invoices;
        this.cashRegister = cashRegister;
        this.paymentAllocationService = paymentAllocationService;
        this.notificationService = notificationService;
    }

    public ApprovePaymentUseCase(PaymentRepository paymentRepository,
            PaymentAllocationRepository allocationRepository,
            PaymentInvoiceGateway invoices,
            CashRegisterGateway cashRegister,
            PaymentAllocationService paymentAllocationService,
            ActivityNotificationRecorder activityNotifications) {
        this(paymentRepository, allocationRepository, invoices, cashRegister,
                paymentAllocationService, null, activityNotifications);
    }

    @Transactional
    @Audited(module = "finance", entityType = "PAYMENT", action = "APPROVE")
    public PaymentResponse execute(UUID id, ApprovePaymentRequest request,
            UUID userId, String userName) {
        Payment payment = paymentRepository.findById(PaymentId.of(id))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Payment not found: " + id));

        cashRegister.requireOpenRegister();
        payment.approve(request.reason(), userId, userName);

        if (payment.getTargetInvoiceId() != null) {
            applyToInvoice(payment, payment.getTargetInvoiceId());
        } else {
            applyToUnpaidInvoicesOldestFirst(payment);
        }

        Payment saved = paymentRepository.save(payment);
        cashRegister.recordMovement(new CashRegisterGateway.Movement(
                CashRegisterGateway.Direction.IN,
                CashRegisterGateway.Type.PAYMENT,
                new CashRegisterGateway.PaymentEntry(saved.getAmount(),
                        saved.getPaymentMethodId(), saved.getNotes(), id,
                        userId, userName)));
        activityNotifications.record(ActivityEvent.of("FINANCE_TREASURY_RECEIVABLES",
                "APPROVE_PAYMENT", "PAYMENT",
                saved.getId().value(), saved.getPaymentNumber().value(), userId));
        if (notificationService != null) {
            notificationService.paymentApproved(
                    payment.getCustomerId(), id);
        }
        return PaymentMapper.toResponse(saved);
    }

    private void applyToInvoice(Payment payment, UUID invoiceId) {
        invoices.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invoice not found: " + invoiceId));

        PaymentAllocation allocation = PaymentAllocation.create(
                payment.getId().value(), invoiceId, payment.getAmount());
        allocationRepository.save(allocation);
        payment.apply(payment.getAmount());
        invoices.recalculateStatus(invoiceId);
    }

    private void applyToUnpaidInvoicesOldestFirst(Payment payment) {
        List<PaymentInvoiceGateway.InvoiceSnapshot> unpaidInvoices = invoices
                .findUnpaidByCustomerId(payment.getCustomerId());

        List<InvoiceRemainingBalance> balances = unpaidInvoices.stream()
                .map(invoice -> new InvoiceRemainingBalance(invoice.id(),
                        invoiceRemainingBalance(invoice)))
                .toList();

        List<PaymentAllocation> allocations = paymentAllocationService
                .allocateOldestFirst(payment.getId().value(), balances,
                        payment.getAmount());

        BigDecimal allocatedTotal = BigDecimal.ZERO;
        for (PaymentAllocation allocation : allocations) {
            allocationRepository.save(allocation);
            invoices.recalculateStatus(allocation.getInvoiceId());
            allocatedTotal = allocatedTotal
                    .add(allocation.getAllocatedAmount());
        }

        if (allocatedTotal.compareTo(BigDecimal.ZERO) > 0) {
            payment.apply(allocatedTotal);
        }
    }

    private BigDecimal invoiceRemainingBalance(
            PaymentInvoiceGateway.InvoiceSnapshot invoice) {
        BigDecimal alreadyAllocated = allocationRepository
                .findByInvoiceId(invoice.id()).stream()
                .map(PaymentAllocation::getAllocatedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return invoice.total().subtract(alreadyAllocated);
    }
}
