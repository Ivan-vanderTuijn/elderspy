package com.polytech.controller;

import com.polytech.connector.AlertForwarder;
import com.polytech.model.AlertMessage;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/gsm-gateway")
public class AlertController {
    @Inject
    @RestClient
    AlertForwarder alertForwarder;

    @POST
    @Path("/alert")
    public void alert(AlertMessage alertMessage) {
        alertForwarder.forwardAlert(alertMessage);
    }
}
