package com.unity.notification_service.service;

import org.springframework.data.domain.Page;

import com.unity.notification_service.dto.NotificationDTO;

public interface NotificationService {
    Page<NotificationDTO> getNotifications(Long userId, int page, int limit);
    void saveNotification(NotificationDTO notificationDTO);
    void markAsRead(Long notificationId);
}
