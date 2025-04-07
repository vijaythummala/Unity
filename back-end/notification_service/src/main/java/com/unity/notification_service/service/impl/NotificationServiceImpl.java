package com.unity.notification_service.service.impl;

import com.unity.notification_service.dto.NotificationDTO;
import com.unity.notification_service.entity.Notification;
import com.unity.notification_service.mapper.NotificationMapper;
import com.unity.notification_service.repository.NotificationRepository;
import com.unity.notification_service.service.NotificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NotificationServiceImpl implements NotificationService{

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public Page<NotificationDTO> getNotifications(Long userId, int page, int limit) {
        PageRequest pageable = PageRequest.of(page, limit);
        Page<Notification> notifications = notificationRepository.findByUserIdAndIsDeletedFalse(userId, pageable);
        return notifications.map(NotificationMapper::toDTO);
    }

    @Override
    public void saveNotification(NotificationDTO notificationDTO) {
        Notification notification = NotificationMapper.toEntity(notificationDTO);
        Notification savedNotification = notificationRepository.save(notification);
        
        messagingTemplate.convertAndSend("/topic/notifications/" + savedNotification.getUserId(), 
            NotificationMapper.toDTO(savedNotification));
    }

    @Override
    public void markAsRead(Long notificationId) {
        Optional<Notification> optionalNotification = notificationRepository.findById(notificationId);
        if (optionalNotification.isPresent()) {
            Notification notification = optionalNotification.get();
            notification.setRead(true);
            notification.setDeleted(true);
            notificationRepository.save(notification);
        }
    }
}
