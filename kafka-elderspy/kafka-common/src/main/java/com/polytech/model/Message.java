package com.polytech.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Message {
    private String id;
    private String content;
    private LocalDateTime timestamp;
}
