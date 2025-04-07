package com.unity.notification_service.controller;

import com.unity.notification_service.constants.NotificationType;
import com.unity.notification_service.dto.NotificationDTO;
import com.unity.notification_service.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/getNotifications")
    public Page<NotificationDTO> getNotifications(
        @RequestParam Long userId, 
        @RequestParam int page, 
        @RequestParam int limit) {
        return notificationService.getNotifications(userId, page, limit);
    }

    @PostMapping("/save")
    public void saveNotification(@RequestParam Long userId, 
                                 @RequestParam String message, 
                                 @RequestParam String type) {
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setUserId(userId);
        notificationDTO.setMessage(message);
        notificationDTO.setType(NotificationType.valueOf(type));
        notificationDTO.setRead(false);
        notificationService.saveNotification(notificationDTO);
    }

    @PutMapping("/mark-read")
    public void markAsRead(@RequestParam Long notificationId) {
        notificationService.markAsRead(notificationId);
    }
}
