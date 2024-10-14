package com.polytech.analyser;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.InfluxDBClientOptions;
import com.influxdb.client.QueryApi;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import io.quarkus.scheduler.Scheduled;
import io.quarkus.scheduler.ScheduledExecution;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

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

    @Getter
    @Setter
    @ConfigProperty(name = "temperature.threshold", defaultValue = "30.0")
    double temperatureThreshold;

    @Getter
    @Setter
    @ConfigProperty(name = "analyse.timespan", defaultValue = "PT5M")
    String analyseTimespan;

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

    @Scheduled(cron = "{cron.expression}")
    public void analyse(ScheduledExecution execution) {
        log.info("coucou3");
        log.info("Starting temperature analysis");
        QueryApi queryApi = influxDBClient.getQueryApi();

        String flux = String.format(
                "from(bucket:\"%s\") " +
                "|> range(start: -%s) " +
                "|> filter(fn: (r) => r._measurement == \"temperature\") " +
                "|> mean()",
                influxdbBucket, analyseTimespan);

        List<FluxTable> result = queryApi.query(flux);

        for (FluxTable table : result) {
            for (FluxRecord record : table.getRecords()) {
                double avgTemp = ((Number) Objects.requireNonNull(record.getValue())).doubleValue();
                log.info("Average temperature: {}", avgTemp);
                if (avgTemp > temperatureThreshold) {
                    emitAlert(avgTemp);
                }
            }
        }
    }

    private void emitAlert(double temperature) {
        log.warn("ALERT: Temperature threshold exceeded. Current average: {}", temperature);
        // TODO : Implement actual alert mechanism here
    }
}
