package com.polytech.service;

import com.polytech.connector.AlertGsmForwarder;
import com.polytech.model.AlertMessage;
import com.polytech.model.AlertRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;

@ApplicationScoped
@Slf4j
public class AlertService {
    @Inject
    @RestClient
    AlertGsmForwarder alertGsmForwarder;

    public List<String> findEntitiesToAlert(AlertMessage alertMessage) {
        log.info("Finding who to alert for: {}", alertMessage);
        return List.of("+33 12 34 56 78 90");
    }

    public void alert(AlertMessage alertMessage) {
        log.info("Alerting: {}", alertMessage);
        List<String> phoneNumbers = findEntitiesToAlert(alertMessage);
        AlertRequest alertRequest = AlertRequest.fromAlertMessage(alertMessage);
        alertRequest.setContacts(phoneNumbers);
        alertGsmForwarder.forwardAlert(alertRequest);
    }
}
