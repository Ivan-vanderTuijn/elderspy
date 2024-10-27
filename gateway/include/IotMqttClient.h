#ifndef IOTCLIENT_H
#define IOTCLIENT_H

#include "mqtt_client.h"
#include "sensors_config.h"
#include "alertManager.h"
#include <string>

class IoTMqttClient : public MqttClient {
public:
    IoTMqttClient(const std::string &address, const std::string &clientId, AlertManager &alertManager);

    void subscribeToAllSensors();

protected:
    void message_arrived(mqtt::const_message_ptr msg) override;

private:
    AlertManager &alertManager;
};

#endif // IOTCLIENT_H
