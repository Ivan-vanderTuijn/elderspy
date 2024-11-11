#include "mqtt_clients/mqtt_client_factory.h"

#include "config/global.h"

std::unique_ptr<MqttClient> MqttClientFactory::createClient(ClientType type,
                                                            MqttClient::MessageCallback messageCallback) {
    const std::string unique_identifier = std::to_string(std::chrono::system_clock::now().time_since_epoch().count());
    if (type == GATEWAY) {
        return createGatewayClient(std::move(messageCallback), unique_identifier);
    } else if (type == BACKEND) {
        return createBackendClient(std::move(messageCallback), unique_identifier);
    }
    return nullptr;
}

std::unique_ptr<MqttClient> MqttClientFactory::createGatewayClient(MqttClient::MessageCallback messageCallback,
                                                                   const std::string &unique_identifier) {
    return std::make_unique<MqttClient>(
        GATEWAY_BROKER_ADDRESS, EDGE_ID + unique_identifier, std::move(messageCallback));
}

std::unique_ptr<MqttClient> MqttClientFactory::createBackendClient(MqttClient::MessageCallback messageCallback,
                                                                   const std::string &unique_identifier) {
    return std::make_unique<MqttClient>(
        BACKEND_BROKER_ADDRESS, EDGE_ID + unique_identifier, std::move(messageCallback), RABBITMQ_DEFAULT_USER,
        RABBITMQ_DEFAULT_PASS
    );
}
