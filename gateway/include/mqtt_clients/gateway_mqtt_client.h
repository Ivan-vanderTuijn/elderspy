#ifndef IOTCLIENT_H
#define IOTCLIENT_H

#include "mqtt_clients/mqtt_client.h"
#include "config/sensors_config.h"
#include "alert/alert_manager.h"
#include <string>

class GatawayMqttClient : public MqttClient {
public:
    GatawayMqttClient(const std::string &address, const std::string &clientId, AlertManager &alertManager);

    void subscribeToAllSensors();

protected:
    void message_arrived(mqtt::const_message_ptr msg) override;

private:
    AlertManager &alertManager;
};

#endif // IOTCLIENT_H
