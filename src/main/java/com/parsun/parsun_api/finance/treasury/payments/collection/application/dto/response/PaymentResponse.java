package com.parsun.parsun_api.finance.treasury.payments.collection.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentResponse(UUID id, String paymentNumber, UUID customerId,
        BigDecimal amount, UUID paymentMethodId, String referenceNumber,
        LocalDate paymentDate, String notes, String status,
        BigDecimal appliedAmount, UUID createdBy, String createdByName,
        LocalDateTime createdAt, LocalDateTime updatedAt,
        String cancelledReason, UUID verifiedBy, String verifiedByName,
        LocalDateTime verifiedAt, UUID targetInvoiceId, String voucherUrl) {

    public PaymentResponse(UUID id, String paymentNumber, UUID customerId,
            BigDecimal amount, UUID paymentMethodId, String referenceNumber,
            LocalDate paymentDate, String notes, String status,
            BigDecimal appliedAmount, UUID createdBy, String createdByName,
            LocalDateTime createdAt, LocalDateTime updatedAt,
            String cancelledReason, UUID verifiedBy, String verifiedByName,
            LocalDateTime verifiedAt) {
        this(id, paymentNumber, customerId, amount, paymentMethodId,
                referenceNumber, paymentDate, notes, status, appliedAmount,
                createdBy, createdByName, createdAt, updatedAt,
                cancelledReason, verifiedBy, verifiedByName, verifiedAt, null,
                null);
    }
}
