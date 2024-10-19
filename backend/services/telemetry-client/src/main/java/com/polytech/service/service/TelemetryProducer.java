package com.polytech.service.service;

import com.polytech.model.TelemetryData;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.subscription.Cancellable;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@ApplicationScoped
public class TelemetryProducer {
    private final Map<String, Cancellable> continuousTelemetry = new ConcurrentHashMap<>();

    // Inject an emitter to send messages to the "telemetry-out" channel
    @Inject
    @Channel("telemetry-channel")
    Emitter<String> telemetryEmitter;

    private final Random random = new Random();

    public TelemetryProducer() {
    }

    // Method to send a single telemetry reading

    public void sendTelemetryData(TelemetryData data) {
        String jsonPayload = JsonObject.mapFrom(data).encode();
        telemetryEmitter.send(jsonPayload);
    }

    // Method to simulate continuous telemetry readings
    public Multi<TelemetryData> generateTelemetryData(String deviceId) {
        return Multi.createFrom().ticks().every(Duration.ofSeconds(1))
                .map(tick -> new TelemetryData(
                        Instant.now(),
                        "edge-1",
                        deviceId,
                        "temperature",
                        String.valueOf(20 + random.nextDouble() * 10)
                ));
    }

    public void startContinuousTelemetry(String deviceId) {
        if (continuousTelemetry.containsKey(deviceId)) {
            throw new IllegalStateException("Continuous telemetry already running for device: " + deviceId);
        }

        Cancellable cancellable = generateTelemetryData(deviceId)
                .subscribe().with(
                        data -> sendTelemetryData(data),
                        throwable -> log.error("Error in continuous telemetry", throwable)
                );

        continuousTelemetry.put(deviceId, cancellable);
    }

    // Stop continuous telemetry for a device
    public void stopContinuousTelemetry(String deviceId) {
        Cancellable cancellable = continuousTelemetry.remove(deviceId);
        if (cancellable != null) {
            cancellable.cancel();
        }
    }
}
