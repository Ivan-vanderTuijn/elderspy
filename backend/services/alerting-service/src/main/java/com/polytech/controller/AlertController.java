package com.polytech.controller;

import com.polytech.model.AlertMessage;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import lombok.extern.slf4j.Slf4j;

@Path("/alerting-service")
@Slf4j
public class AlertController {

    @POST
    @Path("/alert")
    public void alert(AlertMessage alertMessage) {
        log.info("Alert received: {}", alertMessage);
    }
}