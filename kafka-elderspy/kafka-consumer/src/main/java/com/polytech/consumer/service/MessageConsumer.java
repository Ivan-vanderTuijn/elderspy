package com.polytech.consumer.service;

import com.polytech.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MessageConsumer {

    @KafkaListener(topics = "demo-topic", groupId = "demo-group")
    public void listen(Message message) {
        log.info("Received message: {}", message);
    }
}