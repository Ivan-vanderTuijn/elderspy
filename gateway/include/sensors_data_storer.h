#ifndef SENSORS_DATA_STORER_H
#define SENSORS_DATA_STORER_H

#include <mqtt_clients/mqtt_client.h>
#include "telemetry_db.h"

class SensorsDataStorer {
public:
    SensorsDataStorer(MqttClient &mqttClient);

private:
    MqttClient &client;

    void onMessage(mqtt::const_message_ptr msg);
};

#endif // SENSORS_DATA_STORER_H
