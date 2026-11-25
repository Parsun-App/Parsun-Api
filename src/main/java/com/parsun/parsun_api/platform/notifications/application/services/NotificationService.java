package com.parsun.parsun_api.platform.notifications.application.services;

import com.parsun.parsun_api.platform.notifications.application.ports.in.CustomerNotificationRecorder;
import com.parsun.parsun_api.platform.notifications.application.ports.out.NotificationDeliveryGateway;
import com.parsun.parsun_api.platform.notifications.application.ports.out.NotificationRecipientGateway;
import com.parsun.parsun_api.platform.notifications.domain.entity.Notification;
import com.parsun.parsun_api.platform.notifications.domain.repository.NotificationRepository;
import com.parsun.parsun_api.platform.notifications.domain.valueobjects.NotificationId;
import com.parsun.parsun_api.platform.notifications.domain.valueobjects.NotificationType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class NotificationService implements CustomerNotificationRecorder {

    private final NotificationRepository notificationRepository;
    private final NotificationRecipientGateway recipientGateway;
    private final NotificationDeliveryGateway deliveryGateway;

    public NotificationService(NotificationRepository notificationRepository,
            NotificationRecipientGateway recipientGateway,
            NotificationDeliveryGateway deliveryGateway) {
        this.notificationRepository = notificationRepository;
        this.recipientGateway = recipientGateway;
        this.deliveryGateway = deliveryGateway;
    }

    @Transactional
    public Notification create(UUID userId, NotificationType type, String title,
            String message, String referenceType, UUID referenceId) {
        Notification notification = Notification.create(userId, type, title,
                message, referenceType, referenceId);
        Notification saved = notificationRepository.save(notification);
        deliveryGateway.deliver(saved);
        return saved;
    }

    @Transactional
    public Notification createForCustomer(UUID customerId,
            NotificationType type, String title, String message,
            String referenceType, UUID referenceId) {
        Notification notification = Notification.createForCustomer(customerId,
                type, title, message, referenceType, referenceId);
        Notification saved = notificationRepository.save(notification);
        deliveryGateway.deliver(saved);
        return saved;
    }

    private Notification createLocalized(UUID userId, UUID customerId,
            NotificationType type, String referenceType, UUID referenceId,
            String... arguments) {
        String key = "notifications."
                + type.name().toLowerCase(Locale.ROOT);
        var notification = Notification
                .create(userId, customerId, type, key + ".title",
                        key + ".message", referenceType, referenceId)
                .withTranslation(key + ".title", key + ".message",
                        java.util.Arrays.asList(arguments));
        var saved = notificationRepository.save(notification);
        deliveryGateway.deliver(saved);
        return saved;
    }

    @Transactional(readOnly = true)
    public List<Notification> listByUser(UUID userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public List<Notification> listByCustomer(UUID customerId) {
        return notificationRepository
                .findByCustomerIdOrderByCreatedAtDesc(customerId);
    }

    @Transactional(readOnly = true)
    public long countUnread(UUID userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }

    @Transactional(readOnly = true)
    public long countUnreadByCustomer(UUID customerId) {
        return notificationRepository.countUnreadByCustomerId(customerId);
    }

    @Transactional
    public Notification markAsRead(UUID id, UUID userId) {
        Notification notification = notificationRepository
                .findById(NotificationId.of(id))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Notification not found: " + id));
        if (!userId.equals(notification.getUserId())) {
            throw new IllegalArgumentException(
                    "Notification does not belong to the current user");
        }
        notification.markAsRead();
        return notificationRepository.save(notification);
    }

    @Transactional
    public Notification markAsReadByCustomer(UUID id, UUID customerId) {
        Notification notification = notificationRepository
                .findById(NotificationId.of(id))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Notification not found: " + id));
        if (!customerId.equals(notification.getCustomerId())) {
            throw new IllegalArgumentException(
                    "Notification does not belong to the current customer");
        }
        notification.markAsRead();
        return notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(UUID userId) {
        notificationRepository.markAllAsReadByUserId(userId);
    }

    @Transactional
    public void delete(UUID id, UUID userId) {
        Notification notification = notificationRepository
                .findById(NotificationId.of(id))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Notification not found: " + id));
        if (!userId.equals(notification.getUserId())) {
            throw new IllegalArgumentException(
                    "Notification does not belong to the current user");
        }
        notificationRepository.deleteById(notification.getId());
    }

    @Transactional
    public void notifyAdminsPaymentPending(UUID paymentId, String customerName,
            BigDecimal amount) {
        for (UUID recipient : recipientGateway.findAllUserIds()) {
            createLocalized(recipient, null,
                    NotificationType.PAYMENT_PENDING_APPROVAL, "PAYMENT",
                    paymentId, customerName, amount.toPlainString());
        }
    }

    @Transactional
    public void notifyAdminsQuoteCreated(UUID quoteId, String customerName) {
        for (UUID recipient : recipientGateway.findAllUserIds()) {
            createLocalized(recipient, null, NotificationType.QUOTE_CREATED,
                    "QUOTE", quoteId, customerName);
        }
    }

    @Transactional
    public void notifyAdminsOrderCreated(UUID orderId, String customerName) {
        for (UUID recipient : recipientGateway.findAllUserIds()) {
            createLocalized(recipient, null, NotificationType.ORDER_CREATED,
                    "ORDER", orderId, customerName);
        }
    }

    @Transactional
    public void notifyCustomerPaymentApproved(UUID customerId,
            UUID paymentId) {
        createLocalized(null, customerId, NotificationType.PAYMENT_APPROVED,
                "PAYMENT", paymentId);
    }

    @Override
    public void paymentApproved(UUID customerId, UUID paymentId) {
        notifyCustomerPaymentApproved(customerId, paymentId);
    }

    @Override
    public void paymentRejected(UUID customerId, UUID paymentId, String reason) {
        notifyCustomerPaymentRejected(customerId, paymentId, reason);
    }

    @Override
    public void quoteCancelled(UUID customerId, UUID quoteId, String reason) {
        notifyCustomerQuoteRejected(customerId, quoteId, reason);
    }

    @Override
    public void quoteConvertedToOrder(UUID customerId, UUID quoteId,
            String orderNumber) {
        notifyCustomerQuoteConvertedToOrder(customerId, quoteId, orderNumber);
    }

    @Override
    public void quoteConvertedToInvoice(UUID customerId, UUID quoteId,
            String invoiceNumber) {
        notifyCustomerQuoteConvertedToInvoice(customerId, quoteId,
                invoiceNumber);
    }

    @Override
    public void orderConfirmed(UUID customerId, UUID orderId) {
        notifyCustomerOrderConfirmed(customerId, orderId);
    }

    @Override
    public void orderCancelled(UUID customerId, UUID orderId, String reason) {
        notifyCustomerOrderCancelled(customerId, orderId, reason);
    }

    @Override
    public void orderConvertedToInvoice(UUID customerId, UUID orderId,
            String invoiceNumber) {
        notifyCustomerOrderConvertedToInvoice(customerId, orderId,
                invoiceNumber);
    }

    @Override
    public void invoiceCreated(UUID customerId, UUID invoiceId,
            String invoiceNumber) {
        notifyCustomerInvoiceCreated(customerId, invoiceId, invoiceNumber);
    }

    @Override
    public void invoiceConfirmed(UUID customerId, UUID invoiceId,
            String invoiceNumber) {
        notifyCustomerInvoiceConfirmed(customerId, invoiceId, invoiceNumber);
    }

    @Override
    public void invoiceCancelled(UUID customerId, UUID invoiceId,
            String invoiceNumber, String reason) {
        notifyCustomerInvoiceCancelled(customerId, invoiceId, invoiceNumber,
                reason);
    }

    @Transactional
    public void notifyCustomerPaymentRejected(UUID customerId, UUID paymentId,
            String reason) {
        createLocalized(null, customerId, NotificationType.PAYMENT_REJECTED,
                "PAYMENT", paymentId, reason);
    }

    @Transactional
    public void notifyCustomerQuoteConvertedToOrder(UUID customerId,
            UUID quoteId, String orderNumber) {
        createLocalized(null, customerId,
                NotificationType.QUOTE_CONVERTED_TO_ORDER, "QUOTE", quoteId,
                orderNumber);
    }

    @Transactional
    public void notifyCustomerQuoteRejected(UUID customerId, UUID quoteId,
            String reason) {
        createLocalized(null, customerId, NotificationType.QUOTE_REJECTED,
                "QUOTE", quoteId, reason);
    }

    @Transactional
    public void notifyCustomerQuoteConvertedToInvoice(UUID customerId,
            UUID quoteId, String invoiceNumber) {
        createLocalized(null, customerId,
                NotificationType.QUOTE_CONVERTED_TO_INVOICE, "QUOTE", quoteId,
                invoiceNumber);
    }

    @Transactional
    public void notifyCustomerOrderConfirmed(UUID customerId, UUID orderId) {
        createLocalized(null, customerId, NotificationType.ORDER_APPROVED,
                "ORDER", orderId);
    }

    @Transactional
    public void notifyCustomerOrderCancelled(UUID customerId, UUID orderId,
            String reason) {
        createLocalized(null, customerId, NotificationType.ORDER_REJECTED,
                "ORDER", orderId, reason);
    }

    @Transactional
    public void notifyCustomerOrderConvertedToInvoice(UUID customerId,
            UUID orderId, String invoiceNumber) {
        createLocalized(null, customerId,
                NotificationType.ORDER_CONVERTED_TO_INVOICE, "ORDER", orderId,
                invoiceNumber);
    }

    @Transactional
    public void notifyCustomerInvoiceCreated(UUID customerId, UUID invoiceId,
            String invoiceNumber) {
        createLocalized(null, customerId, NotificationType.INVOICE_CREATED,
                "INVOICE", invoiceId, invoiceNumber);
    }

    @Transactional
    public void notifyCustomerInvoiceConfirmed(UUID customerId, UUID invoiceId,
            String invoiceNumber) {
        createLocalized(null, customerId, NotificationType.INVOICE_CONFIRMED,
                "INVOICE", invoiceId, invoiceNumber);
    }

    @Transactional
    public void notifyCustomerInvoiceCancelled(UUID customerId, UUID invoiceId,
            String invoiceNumber, String reason) {
        createLocalized(null, customerId, NotificationType.INVOICE_CANCELLED,
                "INVOICE", invoiceId, invoiceNumber, reason);
    }
}
