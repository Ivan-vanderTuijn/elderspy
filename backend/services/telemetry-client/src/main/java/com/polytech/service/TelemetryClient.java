package com.polytech.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.polytech.model.TelemetryData;
import com.rabbitmq.client.Channel;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Random;

@Slf4j
@ApplicationScoped
public class TelemetryClient {

    @ConfigProperty(name = "rabbitmq.host")
    String rabbitmqHost;

    @ConfigProperty(name = "rabbitmq.port")
    int rabbitmqPort;

    @ConfigProperty(name = "rabbitmq.username")
    String rabbitmqUsername;

    @ConfigProperty(name = "rabbitmq.password")
    String rabbitmqPassword;

    @Inject
    ObjectMapper objectMapper;

    private Channel channel;
    private final Random random = new Random();
    private static final String EXCHANGE_NAME = "telemetry_exchange";
    private static final String ROUTING_KEY = "telemetry.data";

    void onStart(@Observes StartupEvent ev) {
        try {
            setupRabbitMQ();
            log.info("TelemetryService started successfully");
        } catch (Exception e) {
            log.error("Failed to initialize RabbitMQ connection", e);
            throw new RuntimeException("Failed to initialize RabbitMQ connection", e);
        }
    }

    void onStop(@Observes ShutdownEvent ev) {
        try {
            if (channel != null) {
                channel.close();
                log.info("RabbitMQ channel closed successfully");
            }
        } catch (Exception e) {
            log.error("Error closing RabbitMQ channel", e);
        }
    }

    private void setupRabbitMQ() throws Exception {
        com.rabbitmq.client.ConnectionFactory factory = new com.rabbitmq.client.ConnectionFactory();
        factory.setHost(rabbitmqHost);
        factory.setPort(rabbitmqPort);
        factory.setUsername(rabbitmqUsername);
        factory.setPassword(rabbitmqPassword);

        var connection = factory.newConnection();
        channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "topic", true);
        log.info("RabbitMQ connection established successfully");
    }

    @Scheduled(every = "1s")
    void generateAndPublishTelemetry() {
        try {
            TelemetryData data = generateTelemetryData();
            publishTelemetryData(data);
            log.debug("Published telemetry data: {}", data);
        } catch (Exception e) {
            log.error("Error publishing telemetry data", e);
        }
    }

    private TelemetryData generateTelemetryData() {
        return TelemetryData.builder()
                .timestamp(Instant.now())
                .deviceId("device-001")
                .edgeId("edge-001")
                .measurement("temperature")
                .value(String.valueOf(random.nextDouble() * 100))
                .build();
    }

    private void publishTelemetryData(TelemetryData data) throws Exception {
        String message = objectMapper.writeValueAsString(data);
        channel.basicPublish(EXCHANGE_NAME,
                ROUTING_KEY,
                null,
                message.getBytes(StandardCharsets.UTF_8));
        log.info("Published telemetry data: {}", data);
    }

    @Liveness
    HealthCheckResponse checkHealth() {
        return HealthCheckResponse.builder()
                .name("TelemetryService")
                .status(channel != null && channel.isOpen())
                .build();
    }
}
