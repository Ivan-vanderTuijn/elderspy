#include <iostream>
#include <mqtt/async_client.h>
#include "mqtt_client.h"
#include "telemetry_db.h"

#include "tcp_handler.h"

using namespace std;

const string SERVER_ADDRESS("tcp://localhost:1883"); //Le port par défaut mqtt de NanoMQ est 1883
const string CLIENT_ID("gateway");
const string TOPIC("house/1/temp/");


const int QOS = 1;
const string PAYLOAD("Payload de test");


// Callback class for MQTT client (only to test)
class callback : public virtual mqtt::callback {
public:
    void connected(const string &cause) override {
        cout << "\nConnected to NanoMQ: " << cause << endl;
    }

    void message_arrived(mqtt::const_message_ptr msg) override {
        cout << "\nMessage arrived on topic: " << msg->get_topic() << endl;
        cout << "Payload: " << msg->to_string() << endl;
    }

    void delivery_complete(mqtt::delivery_token_ptr token) override {
        cout << "\nDelivery complete for token: " << (token ? token->get_message_id() : -1) << endl;
    }
};

int main() {
    TelemetryDB &telemetryDB = TelemetryDB::getInstance();

    // Log tables content using the TelemetryDB singleton

    auto houseTemperatureData = telemetryDB.getHouseTemperature();
    for (const auto &row: houseTemperatureData) {
        for (const auto &col: row) {
            std::cout << col << " ";
        }
        std::cout << std::endl;
    }

    auto heartRateData = telemetryDB.getHeartRate();
    for (const auto &row: heartRateData) {
        for (const auto &col: row) {
            std::cout << col << " ";
        }
        std::cout << std::endl;
    }

    // MqttClient mqttClient(SERVER_ADDRESS, CLIENT_ID);
    //
    // try {
    //     mqttClient.connect();
    //     mqttClient.subscribe(TOPIC, QOS);
    //     mqttClient.publish(TOPIC, PAYLOAD, QOS);
    //
    //     mqttClient.wait_for_messages(); // Keep the client alive
    // }
    // catch (const mqtt::exception& exc) {
    //     cerr << "MQTT error: " << exc.what() << endl;
    //     return 1;
    // }


    return 0;
}

