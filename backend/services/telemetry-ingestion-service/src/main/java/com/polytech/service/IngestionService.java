package com.polytech.service;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.polytech.model.TelemetryData;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;


@Slf4j
@ApplicationScoped
public class IngestionService {
    @ConfigProperty(name = "influxdb.url", defaultValue = "http://localhost:8086")
    String influxdbUrl;

    @ConfigProperty(name = "influxdb.token", defaultValue = "your-super-secret-auth-token")
    String influxdbToken;

    @ConfigProperty(name = "influxdb.org", defaultValue = "telemetry-org")
    String influxdbOrg;

    @ConfigProperty(name = "influxdb.bucket", defaultValue = "telemetry-bucket")
    String influxdbBucket;

    private InfluxDBClient influxDBClient;

    @Inject
    public IngestionService() {
    }

    @PostConstruct
    void init() {
        log.info("Creating InfluxDB client");
        log.info("InfluxDB URL: {}", influxdbUrl);
        log.info("InfluxDB Org: {}", influxdbOrg);
        log.info("InfluxDB Bucket: {}", influxdbBucket);
        log.info("InfluxDB Token: {}", influxdbToken);

        influxDBClient = InfluxDBClientFactory.create(
                influxdbUrl,
                influxdbToken.toCharArray()
        );

        // Verify connection on startup
        try {
            influxDBClient.ping();
            log.info("Successfully connected to InfluxDB");
        } catch (Exception e) {
            log.error("Failed to connect to InfluxDB", e);
        }
    }

    public void storeTelemetryData(TelemetryData data) {
        try (WriteApi writeApi = influxDBClient.makeWriteApi()) {
            Point point = Point.measurement(data.getMeasurement())
                    .addTag("edgeId", data.getDeviceId())
                    .addTag("deviceId", data.getEdgeId())
                    .addField("value", data.getValue())
                    .time(data.getTimestamp(), WritePrecision.MS);

            writeApi.writePoint(influxdbBucket, influxdbOrg, point);
            log.info("Successfully stored telemetry data for device {}", data.getDeviceId());
        } catch (Exception e) {
            log.error("Failed to store telemetry data: {}", e.getMessage());
        }
    }
}
