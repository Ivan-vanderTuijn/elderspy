package com.polytech.connector;

import com.polytech.model.AlertMessage;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;

@Path("/alerting-service")
@RestClient
@RegisterRestClient(configKey = "alerting-service-api")
public interface AlertForwarder {
    @POST
    @Path("/alert")
    List<String> forwardAlert(AlertMessage alertMessage);
}
