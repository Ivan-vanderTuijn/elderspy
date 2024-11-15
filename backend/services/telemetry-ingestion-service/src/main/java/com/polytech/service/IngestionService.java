package com.polytech.service;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.polytech.model.TelemetryData;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Random;


@Slf4j
@ApplicationScoped
public class IngestionService {
    @ConfigProperty(name = "influxdb.url", defaultValue = "http://influxdb:8086")
    String influxdbUrl;

    @ConfigProperty(name = "influxdb.token", defaultValue = "my-super-secret-auth-token")
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
        log.info("Creating InfluxDB client with URL: {}, Org: {}, Bucket: {}, Token: {}", influxdbUrl, influxdbOrg, influxdbBucket, influxdbToken);
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
        try {
            Point point = Point.measurement(data.getMeasurement())
                    .addTag("edgeId", data.getEdgeId())
                    .addTag("deviceId", data.getDeviceId())
                    .addField("value", Double.parseDouble(data.getValue()))
                    .time(data.getTimestamp(), WritePrecision.MS);

            WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
            writeApi.writePoint(influxdbBucket, influxdbOrg, point);
            log.info("Successfully stored telemetry data for device {} {}", data.getDeviceId(), data);
        } catch (Exception e) {
            log.error("Failed to store telemetry data: {}", e.getMessage());
        }
    }
}
