#ifndef ALERTMANAGER_H
#define ALERTMANAGER_H

#include "config/sensors_config.h"
#include <string>
#include <map>
#include <cpprest/http_client.h>  // Requires C++ REST SDK or a similar HTTP library
#include <mqtt_clients/mqtt_client.h>

class AlertManager : public mqtt::callback {
public:
    AlertManager(const std::string &backendUrl);

    // Override the message callback from the mqtt::callback base class
    void onMessage(mqtt::const_message_ptr msg);

private:
    std::unique_ptr<MqttClient> client;
    std::string backendUrl; // Backend URL for REST requests


    void sendAlert(const AlertSeverity &severity, const std::string &deviceId, const std::string &timestamp,
                   const std::string &message);

    std::string getSeverityString(AlertSeverity severity);
};

#endif // ALERTMANAGER_H
