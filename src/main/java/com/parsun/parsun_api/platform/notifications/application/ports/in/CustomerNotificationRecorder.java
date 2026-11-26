package com.parsun.parsun_api.platform.notifications.application.ports.in;

import java.util.UUID;

public interface CustomerNotificationRecorder {

    void paymentApproved(UUID customerId, UUID paymentId);

    void paymentRejected(UUID customerId, UUID paymentId, String reason);

    void quoteCancelled(UUID customerId, UUID quoteId, String reason);

    void quoteConvertedToOrder(UUID customerId, UUID quoteId,
            String orderNumber);

    void quoteConvertedToInvoice(UUID customerId, UUID quoteId,
            String invoiceNumber);

    void orderConfirmed(UUID customerId, UUID orderId);

    void orderCancelled(UUID customerId, UUID orderId, String reason);

    void orderConvertedToInvoice(UUID customerId, UUID orderId,
            String invoiceNumber);

    void invoiceCreated(UUID customerId, UUID invoiceId,
            String invoiceNumber);

    void invoiceConfirmed(UUID customerId, UUID invoiceId,
            String invoiceNumber);

    void invoiceCancelled(UUID customerId, UUID invoiceId,
            String invoiceNumber, String reason);
}
