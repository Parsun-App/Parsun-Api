package com.parsun.parsun_api.finance.treasury.payments.collection.application.queries.use_case;

import com.parsun.parsun_api.finance.treasury.payments.collection.application.dto.response.PaymentResponse;
import com.parsun.parsun_api.finance.treasury.payments.collection.application.mappers.PaymentMapper;
import com.parsun.parsun_api.finance.treasury.payments.collection.domain.repository.PaymentRepository;
import com.parsun.parsun_api.finance.treasury.payments.collection.domain.valueobjects.PaymentId;

import java.util.UUID;

public class GetPaymentByIdUseCase {

    private final PaymentRepository paymentRepository;

    public GetPaymentByIdUseCase(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public PaymentResponse execute(UUID id) {
        return paymentRepository.findById(PaymentId.of(id))
                .map(PaymentMapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Payment not found: " + id));
    }
}
