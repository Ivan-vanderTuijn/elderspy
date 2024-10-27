#include "../include/IotMqttClient.h"
#include <iostream>

#include "sensors_config.h"

IoTMqttClient::IoTMqttClient(const std::string &address, const std::string &clientId, AlertManager &alertManager)
    : MqttClient(address, clientId), alertManager(alertManager) {
}


void IoTMqttClient::subscribeToAllSensors() {
    for (const auto &sensorConfig: sensorConfigs) {
        this->subscribe(sensorConfig.second.topic, 1);
    }
}

void IoTMqttClient::message_arrived(mqtt::const_message_ptr msg) {
    std::string topic = msg->get_topic();
    std::string payload = msg->to_string();

    std::cout << "\nIoTClient received message on topic: " << topic << std::endl;
    std::cout << "Payload: " << payload << std::endl;

    try {
        SensorType type = getSensorTypeByTopic(topic);
        alertManager.handleSensorData(type, payload);
    } catch (const std::invalid_argument &e) {
        std::cout << "Received message on unhandled topic: " << topic << std::endl;
    }
}
