package com.unity.account_service.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.unity.account_service.dto.NotificationDTO;

@Service
public class NotificationProducer {

    @Autowired
    private KafkaTemplate<String, NotificationDTO> kafkaTemplate;

    public void sendNotification(NotificationDTO event) {
        kafkaTemplate.send("notification-topic", event);
    }
}

