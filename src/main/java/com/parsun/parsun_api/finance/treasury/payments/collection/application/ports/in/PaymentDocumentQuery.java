package com.parsun.parsun_api.finance.treasury.payments.collection.application.ports.in;
import java.util.UUID;
public interface PaymentDocumentQuery {
    PaymentDocumentSnapshot find(UUID id);
}
