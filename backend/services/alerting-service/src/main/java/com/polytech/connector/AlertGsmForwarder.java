package com.polytech.connector;

import com.polytech.model.AlertMessage;
import com.polytech.model.AlertRequest;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;

@Path("/gsm-gateway")
@RestClient
@RegisterRestClient(configKey = "gsm-gateway-service-api")
public interface AlertGsmForwarder {

    @POST
    @Path("/internal-alert")
    List<String> forwardAlert(AlertRequest alertRequest);
}
