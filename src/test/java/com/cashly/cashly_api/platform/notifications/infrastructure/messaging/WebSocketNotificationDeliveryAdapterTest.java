package com.cashly.cashly_api.platform.notifications.infrastructure.messaging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.cashly.cashly_api.platform.auth.preferences.domain.repository.UserPreferencesRepository;
import com.cashly.cashly_api.platform.notifications.domain.entity.Notification;
import com.cashly.cashly_api.platform.notifications.domain.valueobjects.NotificationType;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

class WebSocketNotificationDeliveryAdapterTest {

    @Test
    void shouldDeliverCustomerNotificationsWithAFlatPayload() {
        SimpMessagingTemplate messagingTemplate = mock(
                SimpMessagingTemplate.class);
        UserPreferencesRepository preferences = mock(
                UserPreferencesRepository.class);
        WebSocketNotificationDeliveryAdapter adapter = new WebSocketNotificationDeliveryAdapter(
                messagingTemplate, preferences);
        UUID customerId = UUID.randomUUID();
        UUID referenceId = UUID.randomUUID();
        Notification notification = Notification.createForCustomer(customerId,
                NotificationType.ORDER_APPROVED, "Pedido aprobado",
                "Tu pedido ha sido aprobado.", "ORDER", referenceId);

        adapter.deliver(notification);

        ArgumentCaptor<Object> payload = ArgumentCaptor.forClass(Object.class);
        verify(messagingTemplate).convertAndSend(
                eq("/topic/notifications/customer/" + customerId),
                payload.capture());
        assertInstanceOf(
                WebSocketNotificationDeliveryAdapter.NotificationMessage.class,
                payload.getValue());
        WebSocketNotificationDeliveryAdapter.NotificationMessage message = (WebSocketNotificationDeliveryAdapter.NotificationMessage) payload
                .getValue();
        assertEquals(notification.getId().value(), message.id());
        assertEquals("ORDER", message.referenceType());
        assertEquals(referenceId, message.referenceId());
        assertFalse(message.isRead());
    }
}
