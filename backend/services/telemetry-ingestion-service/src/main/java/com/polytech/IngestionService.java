package com.polytech;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import io.micrometer.core.instrument.MeterRegistry;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.nio.charset.StandardCharsets;


@Slf4j
public class IngestionService {
    @ConfigProperty(name = "influxdb.url")
    String influxdbUrl;

    @ConfigProperty(name = "influxdb.token")
    String influxdbToken;

    @ConfigProperty(name = "influxdb.org")
    String influxdbOrg;

    @ConfigProperty(name = "influxdb.bucket")
    String influxdbBucket;

    @ConfigProperty(name = "rabbitmq.host")
    String rabbitmqHost;

    @ConfigProperty(name = "rabbitmq.port")
    int rabbitmqPort;

    @ConfigProperty(name = "rabbitmq.username")
    String rabbitmqUsername;

    @ConfigProperty(name = "rabbitmq.password")
    String rabbitmqPassword;

    @Inject
    MeterRegistry registry;

    @Inject
    ObjectMapper objectMapper;

    private InfluxDBClient influxDBClient;
    private Channel channel;
    private static final String EXCHANGE_NAME = "telemetry_exchange";
    private static final String ROUTING_KEY = "telemetry.data";


    void onStart(@Observes StartupEvent ev) {
        try {
            setupInfluxDB();
            setupRabbitMQ();
            log.info("Telemetry ingestion service started successfully");
        } catch (Exception e) {
            log.error("Failed to initialize services", e);
            throw new RuntimeException("Failed to initialize services", e);
        }
    }

    void onStop(@Observes ShutdownEvent ev) {
        try {
            if (channel != null) {
                channel.close();
                log.info("RabbitMQ channel closed successfully");
            }
            if (influxDBClient != null) {
                influxDBClient.close();
                log.info("InfluxDB connection closed successfully");
            }
        } catch (Exception e) {
            // Log shutdown errors
            log.error("Error closing services", e);
            e.printStackTrace();
        }
    }

    private void setupInfluxDB() {
        influxDBClient = InfluxDBClientFactory.create(
                influxdbUrl,
                influxdbToken.toCharArray(),
                influxdbOrg,
                influxdbBucket);
        log.info("InfluxDB connection established successfully");
    }

    private void setupRabbitMQ() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(rabbitmqHost);
        factory.setPort(rabbitmqPort);
        factory.setUsername(rabbitmqUsername);
        factory.setPassword(rabbitmqPassword);

        var connection = factory.newConnection();
        channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "topic", true);
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME, ROUTING_KEY);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            storeTelemetryData(message);

            // Update metrics
            registry.counter("telemetry.messages.processed").increment();
        };

        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});
        log.info("RabbitMQ connection established successfully");
    }

    private void storeTelemetryData(String message) {
        try {
            JsonNode telemetryData = objectMapper.readTree(message);

            Point point = Point.measurement(telemetryData.get("measurement").asText())
                    .time(telemetryData.get("timestamp").asLong(), WritePrecision.MS)
                    .addTag("edgeId", telemetryData.get("edgeId").asText())
                    .addTag("deviceId", telemetryData.get("deviceId").asText())
                    .addField("value", telemetryData.get("value").asDouble());

            try (WriteApi writeApi = influxDBClient.makeWriteApi()) {
                writeApi.writePoint(point);
                log.info("Stored telemetry data: {}", telemetryData);
                registry.counter("telemetry.points.stored").increment();
            }

        } catch (Exception e) {
            registry.counter("telemetry.errors").increment();
            e.printStackTrace();
        }
    }
}
