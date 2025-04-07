package com.unity.notification_service.mapper;

import com.unity.notification_service.dto.NotificationDTO;
import com.unity.notification_service.entity.Notification;

public class NotificationMapper {

    public static NotificationDTO toDTO(Notification notification) {
        if (notification == null) {
            return null;
        }

        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setUserId(notification.getUserId());
        notificationDTO.setType(notification.getType());
        notificationDTO.setMessage(notification.getMessage());
        notificationDTO.setRead(notification.isRead());

        return notificationDTO;
    }

    public static Notification toEntity(NotificationDTO notificationDTO) {
        if (notificationDTO == null) {
            return null;
        }

        Notification notification = new Notification();
        notification.setUserId(notificationDTO.getUserId());
        notification.setType(notificationDTO.getType());
        notification.setMessage(notificationDTO.getMessage());
        notification.setRead(notificationDTO.isRead());

        return notification;
    }
}
