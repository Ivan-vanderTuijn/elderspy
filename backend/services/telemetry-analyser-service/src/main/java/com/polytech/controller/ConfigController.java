package com.polytech.controller;

import com.cronutils.validation.Cron;
import com.cronutils.validation.CronValidator;
import com.polytech.analyser.TemperatureAnalyser;
import com.polytech.dti.Configuration;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.quartz.CronExpression;

@ApplicationScoped
@Path("/config")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
public class ConfigController {

    @Inject
    TemperatureAnalyser temperatureAnalyser;

    @ConfigProperty(name = "cron.expression", defaultValue = "0 */5 * * * ?")
    String cronExpression;

    public ConfigController() {
    }

    @GET
    @Path("/temperature")
    public Configuration getAnalysisConfiguration() {
        log.info("Returning analysis configuration");
        return new Configuration(cronExpression, temperatureAnalyser.getAnalyseTimespan(), temperatureAnalyser.getTemperatureThreshold());
    }

    @POST
    @Path("/temperature")
    public Response configureAnalysis(Configuration configuration) {
        log.info("Updating analysis configuration");
        String frequency = configuration.getFrequency();
        String timespan = configuration.getTimespan();
        Double threshold = configuration.getThreshold();

        if (validateFrequency(frequency)) {
            this.cronExpression = frequency;
            log.info("Analysis frequency updated to: {}", frequency);
        }
        if (validateTimespan(timespan)) {
            temperatureAnalyser.setAnalyseTimespan(timespan);
            log.info("Analysis timespan updated to: {}", timespan);
        }
        if (validateThreshold(threshold)) {
            temperatureAnalyser.setTemperatureThreshold(threshold);
            log.info("Temperature threshold updated to: {}", threshold);
        }
        log.info("Configuration updated successfully");
        return Response.ok()
                .type(MediaType.APPLICATION_JSON)
                .entity(new JsonObject()
                        .put("message", "Configuration updated successfully")
                        .toString())
                .build();
    }

    private boolean validateFrequency(String frequency) {
        if (CronExpression.isValidExpression(frequency)) {
            return true;
        } else {
            log.error("Invalid frequency format: {}", frequency);
        }
        return false;
    }

    private boolean validateTimespan(String timespan) {
        if (timespan.matches("([0-9]+)(s|m|h)")) {
            return true;
        } else {
            log.error("Invalid timespan format: {}", timespan);
        }
        return false;
    }

    private boolean validateThreshold(Double threshold) {
        if (threshold < 0) {
            log.error("Invalid threshold value: {}", threshold);
        }
        return threshold >= 0;
    }
}
