#ifndef ALERTMANAGER_H
#define ALERTMANAGER_H

#include "config/sensors_config.h"
#include <string>
#include <map>
#include <cpprest/http_client.h>  // Requires C++ REST SDK or a similar HTTP library

class AlertManager {
public:
    AlertManager(const std::string &backendUrl);

    void handleSensorData(SensorType type, const std::string &payload);

private:
    std::string backendUrl; // Backend URL for REST requests

    bool isAboveThreshold(SensorType type, double value);

    void sendAlertToBackend(SensorType type, double value);
};

#endif // ALERTMANAGER_H
