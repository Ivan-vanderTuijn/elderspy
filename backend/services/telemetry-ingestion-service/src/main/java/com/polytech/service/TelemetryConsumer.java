package com.polytech.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.polytech.model.TelemetryData;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.mqtt.MqttMessage;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
@Slf4j
public class TelemetryConsumer {

    private final IngestionService ingestionService;

    private ObjectMapper mapper = new ObjectMapper();


    public TelemetryConsumer(IngestionService ingestionService) {
        this.ingestionService = ingestionService;
        mapper.registerModule(new JavaTimeModule()); // To handle Instant deserialization
    }

    @Incoming("telemetry-channel")
    public Uni<Void> receiveTelemetryData(MqttMessage<byte[]> message) {
        return Uni.createFrom().item(message)
                .onItem().transform(msg -> new String(msg.getPayload()))
                .onItem().transform(payload -> {
                    try {
                        return mapper.readValue(payload, TelemetryData.class);
                    } catch (Exception e) {
                        log.error("Error deserializing MQTT message", e);
                        throw new RuntimeException("Error processing MQTT message", e);
                    }
                })
                .onItem().invoke(telemetryData -> {
                    log.info("Received telemetry data: {}", telemetryData);
                    ingestionService.storeTelemetryData(telemetryData);
                })
                .onFailure().invoke(e -> log.error("Error processing MQTT message", e))
                .replaceWithVoid();
    }
}