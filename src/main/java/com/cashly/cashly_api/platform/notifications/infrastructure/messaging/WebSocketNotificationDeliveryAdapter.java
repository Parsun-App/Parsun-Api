package com.cashly.cashly_api.platform.notifications.infrastructure.messaging;

import com.cashly.cashly_api.platform.notifications.application.ports.out.NotificationDeliveryGateway;
import com.cashly.cashly_api.platform.notifications.domain.entity.Notification;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cashly.cashly_api.platform.auth.preferences.domain.repository.UserPreferencesRepository;
@Component
public class WebSocketNotificationDeliveryAdapter
        implements
            NotificationDeliveryGateway {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(WebSocketNotificationDeliveryAdapter.class);
    private final SimpMessagingTemplate messagingTemplate;
    private final com.cashly.cashly_api.platform.auth.preferences.domain.repository.UserPreferencesRepository preferences;
    private final NotificationTranslator translator = new NotificationTranslator();

    public WebSocketNotificationDeliveryAdapter(
            SimpMessagingTemplate messagingTemplate,
            com.cashly.cashly_api.platform.auth.preferences.domain.repository.UserPreferencesRepository preferences) {
        this.preferences = preferences;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void deliver(Notification notification) {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager
                    .registerSynchronization(new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            try {
                                send(notification);
                            } catch (RuntimeException exception) {
                                LOGGER.warn(
                                        "WebSocket delivery failed for persisted notification {}",
                                        notification.getId(), exception);
                            }
                        }
                    });
        } else {
            send(notification);
        }
    }

    private void send(Notification notification) {
        if (notification.getUserId() == null) {
            if (notification.getCustomerId() != null) {
                messagingTemplate.convertAndSend(
                        "/topic/notifications/customer/"
                                + notification.getCustomerId(),
                        payload(notification,
                                java.util.Locale.forLanguageTag("es")));
            }
            return;
        }
        String destination = "/topic/notifications/user/"
                + notification.getUserId();
        String language = preferences.languageFor(notification.getUserId());
        messagingTemplate.convertAndSend(destination,
                payload(notification, java.util.Locale
                        .forLanguageTag("en".equals(language) ? "en" : "es")));
    }

    private NotificationMessage payload(Notification notification,
            java.util.Locale locale) {
        Notification rendered = translator.render(notification, locale);
        return new NotificationMessage(
                rendered.getId().value(),
                rendered.getType().name(),
                rendered.getTitle(),
                rendered.getMessage(),
                rendered.getReferenceType(),
                rendered.getReferenceId(),
                rendered.isRead(),
                rendered.getCreatedAt(),
                rendered.getReadAt());
    }

    public record NotificationMessage(java.util.UUID id, String type,
            String title, String message, String referenceType,
            java.util.UUID referenceId, boolean isRead,
            java.time.LocalDateTime createdAt,
            java.time.LocalDateTime readAt) {
    }
}
