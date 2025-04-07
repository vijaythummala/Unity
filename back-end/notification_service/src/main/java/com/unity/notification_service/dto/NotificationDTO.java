package com.unity.notification_service.dto;

import com.unity.notification_service.constants.NotificationType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationDTO {
    private Long userId;
    private NotificationType type;
    private String message;
    private boolean isRead;
}
