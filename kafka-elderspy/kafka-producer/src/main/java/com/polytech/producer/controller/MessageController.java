package com.polytech.producer.controller;

import com.polytech.model.Message;
import com.polytech.producer.service.MessageProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageProducer messageProducer;

    @PostMapping
    public void sendMessage(@RequestBody Message message) {
        messageProducer.sendMessage(message);
    }
}