package com.parsun.parsun_api.procurement.purchases.application.ports.out;

import java.math.BigDecimal;
import java.util.UUID;

public interface PurchasePaymentQuery {
    BigDecimal findPaidAmount(UUID purchaseId);
}
