package com.polytech.controller;

import com.polytech.connector.AlertForwarder;
import com.polytech.connector.AlertSender;
import com.polytech.model.AlertMessage;
import com.polytech.model.AlertRequest;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;

@Path("/gsm-gateway")
@Slf4j
public class GmsGatewayEndpoint {
    @Inject
    @RestClient
    AlertForwarder alertForwarder;

    @Inject
    @RestClient
    AlertSender alertSender;

    @ConfigProperty(name = "free.sms.api.user")
    String apiUser;

    @ConfigProperty(name = "free.sms.api.pass")
    String apiPass;

    @POST
    @Path("/alert")
    public void alertFromEdgeAnalysis(AlertMessage alertMessage) {
        List<String> phoneNumbers = alertForwarder.forwardAlert(alertMessage);

        validateApiCredentials();

        try {
            // For POC, we will send the alert to a personal phone number using the Free SMS API
            alertSender.sendAlert(apiUser, apiPass, alertMessage.getMessage());
        } catch (Exception e) {
            log.error("Failed to send alert", e);
            throw new RuntimeException("Failed to send alert", e);
        }
    }

    @POST
    @Path("/internal-alert")
    public void alertFromBackendAnalysis(AlertRequest alertRequest) {
        validateApiCredentials();

        try {
            // For POC, we will send the alert to a personal phone number using the Free SMS API
            // So we will ignore the contacts from AlertRequest and send the alert to the Free SMS api with creds mapped to personal phone number in the .env file
            alertSender.sendAlert(apiUser, apiPass, alertRequest.getMessage());
        } catch (Exception e) {
            log.error("Failed to send alert", e);
            throw new RuntimeException("Failed to send alert", e);
        }
    }

    private void validateApiCredentials() {
        if (apiUser == null || apiPass == null) {
            log.error("API credentials not properly configured");
            throw new IllegalStateException("API credentials not configured");
        }
    }
}
