package com.polytech.service;

import com.polytech.model.TelemetryData;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
@Slf4j
public class TelemetryConsumer {

    private final IngestionService ingestionService;

    public TelemetryConsumer(IngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @Incoming("telemetry-channel")
    public void receiveTelemetryData(JsonObject data) {
        TelemetryData telemetryData = data.mapTo(TelemetryData.class);
        log.info("Received telemetry data: {}", telemetryData);

        // Store the data in InfluxDB
        ingestionService.storeTelemetryData(telemetryData);
    }
}