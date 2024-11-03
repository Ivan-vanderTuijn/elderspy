#include "mqtt_clients/mqtt_client_factory.h"

#include "config/global.h"

std::unique_ptr<MqttClient> MqttClientFactory::createClient(ClientType type,
                                                            MqttClient::MessageCallback messageCallback) {
    if (type == GATEWAY) {
        return createGatewayClient(std::move(messageCallback));
    } else if (type == BACKEND) {
        return createBackendClient(std::move(messageCallback));
    }
    return nullptr;
}

std::unique_ptr<MqttClient> MqttClientFactory::createGatewayClient(MqttClient::MessageCallback messageCallback) {
    return std::make_unique<MqttClient>(
        GATEWAY_BROKER_ADDRESS, EDGE_ID + "SensorsDataForwarder", std::move(messageCallback)
    );
}

std::unique_ptr<MqttClient> MqttClientFactory::createBackendClient(MqttClient::MessageCallback messageCallback) {
    return std::make_unique<MqttClient>(
        BACKEND_BROKER_ADDRESS, EDGE_ID + "SensorsDataForwarder", std::move(messageCallback), RABBITMQ_DEFAULT_USER,
        RABBITMQ_DEFAULT_PASS
    );
}
