package com.polytech.connector;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/sendmsg")
@RestClient
@RegisterRestClient(configKey = "free-sms-api")
public interface AlertSender {
    @GET
    String sendAlert(@QueryParam("user") String user,
                     @QueryParam("pass") String pass,
                     @QueryParam("msg") String message);
}
