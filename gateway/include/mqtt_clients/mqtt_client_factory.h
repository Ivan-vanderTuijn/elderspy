#ifndef MQTTCLIENTFACTORY_H
#define MQTTCLIENTFACTORY_H

#include "mqtt_client.h"
#include <memory>

class MqttClientFactory {
public:
    enum ClientType { GATEWAY, BACKEND };

    static std::unique_ptr<MqttClient> createClient(ClientType type,
                                                    MqttClient::MessageCallback messageCallback = nullptr);

private:
    static std::unique_ptr<MqttClient> createGatewayClient(MqttClient::MessageCallback messageCallback,
                                                           const std::string &unique_identifier);

    static std::unique_ptr<MqttClient> createBackendClient(MqttClient::MessageCallback messageCallback,
                                                           const std::string &unique_identifier);
};

#endif // MQTTCLIENTFACTORY_H
