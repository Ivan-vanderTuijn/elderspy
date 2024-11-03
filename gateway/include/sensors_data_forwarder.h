//
// Created by dorian on 11/1/24.
//

#ifndef SENSORS_DATA_FORWARDER_H
#define SENSORS_DATA_FORWARDER_H
#include <mqtt_clients/mqtt_client.h>


class SensorsDataForwarder {
public:
    SensorsDataForwarder(MqttClient &gatewayClient, MqttClient &backendClient);

private:
    MqttClient &gatewayClient;
    MqttClient &backendClient;

    void onMessage(mqtt::const_message_ptr msg);
};


#endif //SENSORS_DATA_FORWARDER_H
