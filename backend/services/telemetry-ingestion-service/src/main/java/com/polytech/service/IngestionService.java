package com.polytech.service;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.polytech.model.TelemetryData;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;


@Slf4j
@ApplicationScoped
public class IngestionService {
    @ConfigProperty(name = "influxdb.url")
    String influxdbUrl;

    @ConfigProperty(name = "influxdb.token")
    String influxdbToken;

    @ConfigProperty(name = "influxdb.org")
    String influxdbOrg;

    @ConfigProperty(name = "influxdb.bucket")
    String influxdbBucket;

    private final InfluxDBClient influxDBClient;

    @Inject
    public IngestionService() {
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
