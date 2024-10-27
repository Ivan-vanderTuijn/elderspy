//
// Created by dorian on 10/27/24.
//

#include "../include/backend_mqtt_client.h"

BackendMqttClient::BackendMqttClient(const std::string &address, const std::string &clientId)
    : MqttClient(address, clientId) {
}


void BackendMqttClient::publishTelemetryData(const TelemetryData &data) {
    std::string payload = data.timestamp + "," + data.edgeId + "," + data.deviceId + "," + data.measurement + "," +
                          std::to_string(data.value);
    publish("telemetry", payload, 1);
}

