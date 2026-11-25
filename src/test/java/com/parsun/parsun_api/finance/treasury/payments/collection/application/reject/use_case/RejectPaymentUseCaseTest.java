package com.parsun.parsun_api.finance.treasury.payments.collection.application.reject.use_case;

import com.parsun.parsun_api.platform.notifications.application.ports.in.ActivityEvent;

import com.parsun.parsun_api.platform.notifications.application.ports.in.ActivityNotificationRecorder;
import com.parsun.parsun_api.finance.treasury.payments.collection.domain.entity.Payment;
import com.parsun.parsun_api.finance.treasury.payments.collection.domain.repository.PaymentRepository;
import com.parsun.parsun_api.finance.treasury.payments.collection.domain.valueobjects.PaymentId;
import com.parsun.parsun_api.finance.treasury.payments.collection.domain.valueobjects.PaymentNumber;
import com.parsun.parsun_api.finance.treasury.payments.collection.domain.valueobjects.PaymentStatus;
import com.parsun.parsun_api.finance.treasury.payments.collection.application.reject.request.RejectPaymentRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RejectPaymentUseCaseTest {
    @Test
    void rejection_is_saved_before_customer_notification_is_sent() {
        PaymentRepository payments = mock(PaymentRepository.class);
        ActivityNotificationRecorder activityNotifications = mock(
                ActivityNotificationRecorder.class);
        RejectPaymentUseCase useCase = new RejectPaymentUseCase(payments,
                activityNotifications);
        UUID customerId = UUID.randomUUID();
        UUID actorId = UUID.randomUUID();
        Payment payment = Payment.create(PaymentNumber.of(2026, 1), customerId,
                new BigDecimal("75.00"), UUID.randomUUID(), "REF-1",
                LocalDate.now(), null, null, null, PaymentStatus.PENDING,
                UUID.randomUUID(), null);
        String reason = "Comprobante no válido";
        when(payments.findById(PaymentId.of(payment.getId().value())))
                .thenReturn(Optional.of(payment));
        when(payments.save(any(Payment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = useCase.execute(payment.getId().value(),
                new RejectPaymentRequest(reason), actorId, "Admin");

        assertEquals(PaymentStatus.REJECTED.name(), response.status());
        verify(payments).save(payment);
        verify(activityNotifications).record(any(ActivityEvent.class));
    }
}
