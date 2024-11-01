#include "sensors_data_storer.h"

using namespace std;

SensorsDataStorer::SensorsDataStorer(MqttClient &mqttClient)
    : client(mqttClient) {
    client.subscribe("sensor/#", 1);
    client.add_message_callback([this](mqtt::const_message_ptr msg) {
        onMessage(msg);
    });
}

void SensorsDataStorer::onMessage(mqtt::const_message_ptr msg) {
    string payload = msg->to_string();
    cout << "Received message: " << payload << endl;
    try {
        // Check the topic to determine what data it is
        string topic = msg->get_topic();
        string tableName = topic.substr(topic.find_last_of("/") + 1);
        //Insert id timestamp payload
        TelemetryDB::getInstance().executeQuery(
            "INSERT INTO " + tableName + " (timestamp, payload) VALUES (datetime('now'), '" + payload + "');");
    } catch (const invalid_argument &e) {
        cerr << "Invalid argument: " << e.what() << endl;
    }
}
