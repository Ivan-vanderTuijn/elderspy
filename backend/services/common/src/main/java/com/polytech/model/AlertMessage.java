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
    private String sensorId;
    private String timestamp;
    private String message;
}
