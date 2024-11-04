package com.polytech.controller;

import com.polytech.model.AlertMessage;
import com.polytech.service.AlertService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Path("/alerting-service")
@Slf4j
public class AlertController {

    @Inject
    AlertService alertService;

    public AlertController() {}

    @POST
    @Path("/alert")
    public void alert(AlertMessage alertMessage) {
        log.info("Alert received: {}", alertMessage);
        alertService.alert(alertMessage);
    }

    @GET
    @Path("/contacts")
    public List<String> getContacts(AlertMessage alertMessage) {
        log.info("Received request for contacts: {}", alertMessage);
        return alertService.findEntitiesToAlert(alertMessage);
    }
}