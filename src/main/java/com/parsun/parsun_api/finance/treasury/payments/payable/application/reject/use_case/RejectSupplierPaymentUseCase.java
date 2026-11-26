package com.parsun.parsun_api.finance.treasury.payments.payable.application.reject.use_case;

import com.parsun.parsun_api.finance.treasury.payments.payable.application.dto.response.SupplierPaymentResponse;
import com.parsun.parsun_api.finance.treasury.payments.payable.application.mappers.SupplierPaymentMapper;
import com.parsun.parsun_api.finance.treasury.payments.payable.domain.entity.SupplierPayment;
import com.parsun.parsun_api.finance.treasury.payments.payable.domain.repository.SupplierPaymentRepository;
import com.parsun.parsun_api.finance.treasury.payments.payable.domain.valueobjects.SupplierPaymentId;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public class RejectSupplierPaymentUseCase {

    private final SupplierPaymentRepository payments;

    public RejectSupplierPaymentUseCase(SupplierPaymentRepository payments) {
        this.payments = payments;
    }

    @Transactional
    public SupplierPaymentResponse execute(UUID id, String reason,
            UUID actorId, String actorName) {
        SupplierPayment payment = payments
                .findById(SupplierPaymentId.of(id))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Supplier payment not found: " + id));
        payment.reject(reason, actorId, actorName);
        return SupplierPaymentMapper.toResponse(payments.save(payment));
    }
}
