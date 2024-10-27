//
// Created by dorian on 10/27/24.
//

#ifndef BACKEND_MQTT_CLIENT_H
#define BACKEND_MQTT_CLIENT_H
#include "mqtt_client.h"
#include "telemetry_data.h"


class BackendMqttClient : public MqttClient {
public:
    BackendMqttClient(const std::string &address, const std::string &clientId);

    void publishTelemetryData(const TelemetryData &data);

protected:
};


#endif //BACKEND_MQTT_CLIENT_H
