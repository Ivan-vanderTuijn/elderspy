package com.polytech.controller;

import com.polytech.analyser.TemperatureAnalyser;
import com.polytech.dti.Configuration;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
@Slf4j
@Path("/config")
public class ConfigController {

    @Inject
    TemperatureAnalyser temperatureAnalyser;

    @ConfigProperty(name = "cron.expression", defaultValue = "0 */5 * * * ?")
    String cronExpression;

    public ConfigController() {}

    @GET
    @Path("/temperature")
    @Produces(MediaType.APPLICATION_JSON)
    public Configuration getAnalysisConfiguration() {
        return new Configuration(cronExpression, temperatureAnalyser.getAnalyseTimespan(), temperatureAnalyser.getTemperatureThreshold());
    }

    @POST
    @Path("/temperature")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String configureAnalysis(Configuration configuration) {
        String frequency = configuration.frequency();
        String timespan = configuration.timespan();
        Double threshold = configuration.threshold();

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
        return "Configuration updated successfully";
    }

    private boolean validateFrequency(String frequency) {
        return frequency.matches("([0-9]+)(s|m|h)");
    }

    private boolean validateTimespan(String timespan) {
        return timespan.matches("([0-9]+)(s|m|h)");
    }

    private boolean validateThreshold(Double threshold) {
        return threshold >= 0;
    }
}
