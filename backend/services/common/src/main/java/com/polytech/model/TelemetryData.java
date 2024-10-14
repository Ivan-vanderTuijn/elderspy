package com.polytech.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TelemetryData {
    private Instant timestamp;
    private String edgeId;
    private String deviceId;
    private String measurement;
    private String value;
}