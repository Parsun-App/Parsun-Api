package com.parsun.parsun_api.finance.treasury.payment_methods.application.queries.use_case;

import com.parsun.parsun_api.finance.treasury.payment_methods.application.dto.response.PaymentMethodResponse;
import com.parsun.parsun_api.finance.treasury.payment_methods.application.mappers.PaymentMethodMapper;
import com.parsun.parsun_api.finance.treasury.payment_methods.domain.entity.PaymentMethod;
import com.parsun.parsun_api.finance.treasury.payment_methods.application.ports.out.PaymentMethodUsageGateway;
import com.parsun.parsun_api.finance.treasury.payment_methods.domain.repository.PaymentMethodRepository;
import com.parsun.parsun_api.finance.treasury.payment_methods.domain.valueobjects.PaymentMethodId;

import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GetPaymentMethodByIdUseCase {

    private final PaymentMethodRepository repository;
    private final PaymentMethodUsageGateway usageGateway;

    public GetPaymentMethodByIdUseCase(PaymentMethodRepository repository,
            PaymentMethodUsageGateway usageGateway) {
        this.repository = repository;
        this.usageGateway = usageGateway;
    }

    public PaymentMethodResponse execute(UUID id) {
        PaymentMethodId paymentMethodId = PaymentMethodId.of(id);
        PaymentMethod paymentMethod = repository.findById(paymentMethodId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Payment method not found: " + id));

        boolean hasAssociatedRecords = usageGateway
                .hasAssociatedRecords(paymentMethodId.value());
        return PaymentMethodMapper.toResponse(paymentMethod,
                hasAssociatedRecords);
    }
}
