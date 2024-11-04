package com.polytech.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertMessage {
    private AlertSeverity severity;
    private String edgeId;
    private String deviceId;
    private String timestamp;
    private String message;
}
