package com.polytech.service;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.InfluxDBClientOptions;
import com.influxdb.client.QueryApi;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import com.polytech.connector.AlertForwarder;
import com.polytech.model.AlertMessage;
import com.polytech.model.AlertSeverity;
import io.quarkus.scheduler.Scheduled;
import io.quarkus.scheduler.ScheduledExecution;
import io.quarkus.scheduler.Scheduler;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Slf4j
@ApplicationScoped
public class TemperatureAnalyser implements TelemetryAnalyser {

    @ConfigProperty(name = "influxdb.url", defaultValue = "http://localhost:8086")
    String influxdbUrl;

    @ConfigProperty(name = "influxdb.token", defaultValue = "my-super-secret-auth-token")
    String influxdbToken;

    @ConfigProperty(name = "influxdb.org", defaultValue = "telemetry-org")
    String influxdbOrg;

    @ConfigProperty(name = "influxdb.bucket", defaultValue = "telemetry-bucket")
    String influxdbBucket;

    @Inject
    @RestClient
    AlertForwarder alertForwarder;

    @Getter
    @Setter
    @ConfigProperty(name = "temperature.threshold", defaultValue = "30.0")
    double temperatureThreshold;

    @Getter
    @Setter
    @ConfigProperty(name = "analyse.timespan", defaultValue = "PT5M")
    String analyseTimespan;

    @ConfigProperty(name = "analyse.interval", defaultValue = "5s")
    String analyseInterval;

    private InfluxDBClient influxDBClient;

    @Inject
    public TemperatureAnalyser() {
    }

    @PostConstruct
    void init() {
        log.info("Creating InfluxDB client");
        InfluxDBClientOptions options = InfluxDBClientOptions.builder()
                .url(influxdbUrl)
                .authenticateToken(influxdbToken.toCharArray())
                .org(influxdbOrg)
                .bucket(influxdbBucket)
                .build();

        influxDBClient = InfluxDBClientFactory.create(options);

        try {
            influxDBClient.ping();
            log.info("Successfully connected to InfluxDB");
        } catch (Exception e) {
            log.error("Failed to connect to InfluxDB", e);
        }
    }

    public void analyse() {
        log.info("Starting temperature analysis");
        QueryApi queryApi = influxDBClient.getQueryApi();

        String flux = String.format(
                "from(bucket:\"%s\") " +
                        "|> range(start: -%s) " +
                        "|> filter(fn: (r) => r._measurement == \"temperature\") " +
                        "|> mean()" +
                        "|> keep(columns: [\"_value\", \"deviceId\", \"edgeId\"])",
                influxdbBucket, analyseTimespan);

        List<FluxTable> result = queryApi.query(flux);

        for (FluxTable table : result) {
            for (FluxRecord record : table.getRecords()) {
                double avgTemp = ((Number) Objects.requireNonNull(record.getValue())).doubleValue();
                String deviceId = (String) record.getValueByKey("deviceId");
                String edgeId = (String) record.getValueByKey("edgeId");
                log.info("Average temperature for deviceId: {}, edgeId: {}: {}", deviceId, edgeId, avgTemp);
                if (avgTemp > temperatureThreshold) {
                    emitAlert(avgTemp, deviceId, edgeId); // Pass deviceId and edgeId to the alert method
                }
            }
        }
    }

    private void emitAlert(double temperature, String deviceId, String edgeId) {
        log.warn("ALERT: {} {} - Temperature threshold exceeded. Current average: {}", edgeId, deviceId, temperature);
        alertForwarder.forwardAlert(new AlertMessage(AlertSeverity.CRITICAL, edgeId, deviceId, Instant.now().toString(), "Temperature threshold exceeded (" + temperature + "°C) for " + deviceId + " on " + edgeId));
    }
}
