//
// Created by dorian on 11/1/24.
//

#include "../include/sensors_data_forwarder.h"
// json
#include <nlohmann/json.hpp>
//async
#include <future>

#include "config/global.h"

using namespace std;
using namespace nlohmann;

SensorsDataForwarder::SensorsDataForwarder(MqttClient &gatewayClient, MqttClient &backendClient)
    : gatewayClient(gatewayClient), backendClient(backendClient) {
    gatewayClient.subscribe("sensor/#", 1);
    gatewayClient.add_message_callback([this](mqtt::const_message_ptr msg) {
        onMessage(msg);
    });
}

void SensorsDataForwarder::onMessage(mqtt::const_message_ptr msg) {
    string topic = msg->get_topic();
    string measurement = topic.substr(topic.find_last_of("/") + 1);

    // Parse JSON payload
    json payload = json::parse(msg->to_string());

    // Retrieve values from JSON
    string deviceId = payload["deviceId"].get<string>();
    float value = payload["value"].get<float>();

    // Genereate the timestamp (the format is 2024-11-01 18:49:24)
    time_t now = time(nullptr);
    char timestamp[21]; // 20 + 1 for null terminator
    strftime(timestamp, 21, "%Y-%m-%dT%H:%M:%SZ", localtime(&now));


    try {
        const json data =
        {
            {"timestamp", timestamp},
            {"edgeId", EDGE_ID},
            {"deviceId", deviceId},
            {"measurement", measurement},
            {"value", value},
        };
        cout << "Forwarding message: " << data.dump() << endl;

        backendClient.publish(BACKEND_TELEMETRY_TOPIC, data.dump(), 0);
    } catch (const json::parse_error &e) {
        cerr << "JSON parse error: " << e.what() << endl;
    } catch (const json::type_error &e) {
        cerr << "JSON type error: " << e.what() << endl;
    } catch (const invalid_argument &e) {
        cerr << "Invalid argument: " << e.what() << endl;
    } catch (const out_of_range &e) {
        cerr << "Out of range error: " << e.what() << endl;
    }
}
