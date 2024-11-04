package com.polytech.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertRequest {
    private List<String> contacts;
    private AlertSeverity severity;
    private String edgeId;
    private String deviceId;
    private String timestamp;
    private String message;

    public static AlertRequest fromAlertMessage(AlertMessage alertMessage) {
        return AlertRequest.builder()
                .severity(alertMessage.getSeverity())
                .edgeId(alertMessage.getEdgeId())
                .deviceId(alertMessage.getDeviceId())
                .timestamp(alertMessage.getTimestamp())
                .message(alertMessage.getMessage())
                .build();
    }
}