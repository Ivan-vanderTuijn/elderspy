package com.polytech;

import com.polytech.model.TelemetryData;
import com.polytech.service.IngestionService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
@Slf4j
public class TelemetryConsumer {

    @Inject
    IngestionService ingestionService;

    @Incoming("telemetry-in")
    public void receiveTelemetryData(TelemetryData data) {
        log.info("Received telemetry data: {}", data);

        // Store the data in InfluxDB
        ingestionService.storeTelemetryData(data);
    }
}