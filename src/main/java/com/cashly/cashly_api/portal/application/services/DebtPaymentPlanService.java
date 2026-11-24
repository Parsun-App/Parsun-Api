package com.cashly.cashly_api.portal.application.services;

import com.cashly.cashly_api.finance.treasury.payments.collection.application.ports.out.PaymentInvoiceGateway;
import com.cashly.cashly_api.finance.treasury.payments.collection.domain.entity.Payment;
import com.cashly.cashly_api.finance.treasury.payments.collection.domain.entity.PaymentAllocation;
import com.cashly.cashly_api.finance.treasury.payments.collection.domain.repository.PaymentAllocationRepository;
import com.cashly.cashly_api.finance.treasury.payments.collection.domain.repository.PaymentRepository;
import com.cashly.cashly_api.finance.treasury.payments.collection.domain.services.PaymentAllocationService;
import com.cashly.cashly_api.finance.treasury.payments.collection.domain.valueobjects.InvoiceRemainingBalance;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DebtPaymentPlanService {

    private static final String STATUS_PENDING = "PENDING";

    private final PaymentInvoiceGateway invoices;
    private final PaymentAllocationRepository allocations;
    private final PaymentRepository payments;
    private final PaymentAllocationService allocationService;

    public DebtPaymentPlanService(PaymentInvoiceGateway invoices,
            PaymentAllocationRepository allocations, PaymentRepository payments,
            PaymentAllocationService allocationService) {
        this.invoices = invoices;
        this.allocations = allocations;
        this.payments = payments;
        this.allocationService = allocationService;
    }

    public BigDecimal outstanding(UUID customerId) {
        return remainingBalances(customerId).stream()
                .map(InvoiceRemainingBalance::remainingAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<PaymentAllocationService.AllocationPlan> plan(UUID customerId,
            BigDecimal amount) {
        List<InvoiceRemainingBalance> balances = remainingBalances(customerId);
        BigDecimal outstanding = balances.stream()
                .map(InvoiceRemainingBalance::remainingAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (amount.compareTo(outstanding) > 0) {
            throw new IllegalArgumentException(
                    "Payment amount exceeds customer's outstanding debt");
        }
        return allocationService.planOldestFirst(balances, amount);
    }

    private List<InvoiceRemainingBalance> remainingBalances(UUID customerId) {
        Map<UUID, BigDecimal> pendingByInvoice = pendingByInvoice(customerId);
        return invoices.findUnpaidByCustomerId(customerId).stream()
                .map(invoice -> new InvoiceRemainingBalance(invoice.id(),
                        invoice.total().subtract(allocated(invoice.id()))
                                .subtract(pendingByInvoice.getOrDefault(
                                        invoice.id(), BigDecimal.ZERO))))
                .filter(balance -> balance.remainingAmount()
                        .compareTo(BigDecimal.ZERO) > 0)
                .toList();
    }

    private Map<UUID, BigDecimal> pendingByInvoice(UUID customerId) {
        Map<UUID, BigDecimal> pending = new HashMap<>();
        for (Payment payment : payments.findAllWithFilters(null, null,
                customerId, STATUS_PENDING)) {
            if (payment.getTargetInvoiceId() != null) {
                pending.merge(payment.getTargetInvoiceId(),
                        payment.getAmount(), BigDecimal::add);
            }
        }
        return pending;
    }

    private BigDecimal allocated(UUID invoiceId) {
        return allocations.findByInvoiceId(invoiceId).stream()
                .map(PaymentAllocation::getAllocatedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
