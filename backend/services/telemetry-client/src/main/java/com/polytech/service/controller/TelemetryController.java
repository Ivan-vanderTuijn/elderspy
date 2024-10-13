package com.polytech.service.controller;

import com.polytech.model.TelemetryData;
import com.polytech.service.service.TelemetryProducer;
import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

@Path("/api/telemetry")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
public class TelemetryController {
    @Inject
    TelemetryProducer producer;

    @POST
    @Path("/send")
    public Response sendTelemetryData(TelemetryData data) {
        try {
            // Ensure timestamp is set if not provided
            if (data.getTimestamp() == null) {
                data.setTimestamp(Instant.now());
            }

            producer.sendTelemetryData(data);
            log.info("Telemetry data sent successfully: {}", data);
            return Response.ok()
                    .type(MediaType.APPLICATION_JSON)
                    .entity(new JsonObject()
                            .put("message", "Telemetry data sent successfully")
                            .put("timestamp", data.getTimestamp().toString())
                            .toString())
                    .build();
        } catch (Exception e) {
            log.error("Error sending telemetry data", e);
            return Response.serverError()
                    .type(MediaType.APPLICATION_JSON)
                    .entity(new JsonObject()
                            .put("error", "Failed to send telemetry data")
                            .put("message", e.getMessage())
                            .toString())
                    .build();
        }
    }

    @POST
    @Path("/device/{deviceId}/start")
    public Response startContinuousTelemetry(@PathParam("deviceId") String deviceId) {
        try {
            producer.startContinuousTelemetry(deviceId);
            log.info("Started continuous telemetry for device: {}", deviceId);
            return Response.ok()
                    .entity("Started continuous telemetry for device: " + deviceId)
                    .build();
        } catch (Exception e) {
            log.error("Error starting continuous telemetry", e);
            return Response.serverError()
                    .entity("Failed to start continuous telemetry: " + e.getMessage())
                    .build();
        }
    }

    @POST
    @Path("/device/{deviceId}/stop")
    public Response stopContinuousTelemetry(@PathParam("deviceId") String deviceId) {
        try {
            producer.stopContinuousTelemetry(deviceId);
            log.info("Stopped continuous telemetry for device: {}", deviceId);
            return Response.ok()
                    .entity("Stopped continuous telemetry for device: " + deviceId)
                    .build();
        } catch (Exception e) {
            log.error("Error stopping continuous telemetry", e);
            return Response.serverError()
                    .entity("Failed to stop continuous telemetry: " + e.getMessage())
                    .build();
        }
    }
}
