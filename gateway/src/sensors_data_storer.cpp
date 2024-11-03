#include "sensors_data_storer.h"
#include <nlohmann/json.hpp>
using namespace nlohmann;
using namespace std;

SensorsDataStorer::SensorsDataStorer(MqttClient &mqttClient)
    : client(mqttClient) {
    client.subscribe("sensor/#", 1);
    client.add_message_callback([this](mqtt::const_message_ptr msg) {
        onMessage(msg);
    });
}

void SensorsDataStorer::onMessage(mqtt::const_message_ptr msg) {
    try {
        // Parse JSON payload
        json payload = json::parse(msg->to_string());

        // Extract topic and table name
        string topic = msg->get_topic();
        string tableName = topic.substr(topic.find_last_of("/") + 1);

        // Retrieve values from JSON
        string deviceId = payload["deviceId"].get<string>();
        float value = payload["value"].get<float>();

        // Construct the query string with proper formatting
        string query = "INSERT INTO " + tableName +
                       " (timestamp, deviceId, value) VALUES (datetime('now'), '" +
                       deviceId + "', " + to_string(value) + ");";

        // Execute query
        TelemetryDB::getInstance().executeQuery(query);
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
