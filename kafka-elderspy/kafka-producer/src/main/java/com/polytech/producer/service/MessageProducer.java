package com.polytech.producer.service;

import com.polytech.model.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageProducer {
    private final KafkaTemplate<String, Message> kafkaTemplate;

    public void sendMessage(Message message) {
        message.setTimestamp(LocalDateTime.now());
        if (message.getId() == null) {
            message.setId(UUID.randomUUID().toString());
        }
        log.info("Sending message: {}", message);
        kafkaTemplate.send("demo-topic", message.getId(), message);
    }

    @Scheduled(fixedRate = 5000)
    public void sendAutomaticMessage() {
        Message message = new Message();
        message.setId(UUID.randomUUID().toString());
        message.setContent("Automatic message at " + LocalDateTime.now());
        message.setTimestamp(LocalDateTime.now());
        sendMessage(message);
    }
}