package com.unity.notification_service.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.unity.notification_service.dto.NotificationDTO;
import com.unity.notification_service.service.NotificationService;

@Service
public class NotificationConsumer {

    @Autowired
    private NotificationService notificationService;

    @KafkaListener(topics = "notification-topic", groupId = "notification-group")
    public void consume(NotificationDTO event) {
        try{
            notificationService.saveNotification(event);
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        
    }
}

