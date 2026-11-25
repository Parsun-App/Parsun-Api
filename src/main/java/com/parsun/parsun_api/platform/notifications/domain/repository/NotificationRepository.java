package com.parsun.parsun_api.platform.notifications.domain.repository;

import com.parsun.parsun_api.platform.notifications.domain.entity.Notification;
import com.parsun.parsun_api.platform.notifications.domain.valueobjects.NotificationId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationRepository {
    Notification save(Notification notification);
    List<Notification> findByUserIdOrderByCreatedAtDesc(UUID userId);
    List<Notification> findByCustomerIdOrderByCreatedAtDesc(UUID customerId);
    long countUnreadByUserId(UUID userId);
    long countUnreadByCustomerId(UUID customerId);
    Optional<Notification> findById(NotificationId id);
    void markAllAsReadByUserId(UUID userId);
    void deleteById(NotificationId id);
}
