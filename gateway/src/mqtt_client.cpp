#include "mqtt_client.h"
#include <iostream>
#include <thread>
#include <chrono>

using namespace std;

MqttClient::MqttClient(const string& address, const string& clientId)
    : client(address, clientId) {
    client.set_callback(*this);
}

void MqttClient::connect() {
    mqtt::connect_options connOpts;
    connOpts.set_clean_session(true);

    try {
        cout << "Connecting to the NanoMQ server..." << endl;
        client.connect(connOpts)->wait();
        cout << "Connected to NanoMQ!" << endl;
    }
    catch (const mqtt::exception& exc) {
        cerr << "Error: " << exc.what() << endl;
        throw;
    }
}

void MqttClient::subscribe(const string& topic, int qos) {
    try {
        cout << "Subscribing to topic: " << topic << endl;
        client.subscribe(topic, qos)->wait();
    }
    catch (const mqtt::exception& exc) {
        cerr << "Error during subscription: " << exc.what() << endl;
    }
}

void MqttClient::publish(const string& topic, const string& payload, int qos) {
    try {
        cout << "Publishing message to NanoMQ: " << payload << endl;
        mqtt::message_ptr pubmsg = mqtt::make_message(topic, payload);
        pubmsg->set_qos(qos);
        client.publish(pubmsg)->wait();
    }
    catch (const mqtt::exception& exc) {
        cerr << "Error during publish: " << exc.what() << endl;
    }
}

void MqttClient::connected(const string& cause) {
    cout << "\nConnected to NanoMQ: " << cause << endl;
}

void MqttClient::message_arrived(mqtt::const_message_ptr msg) {
    cout << "\nMessage arrived on topic: " << msg->get_topic() << endl;
    cout << "Payload: " << msg->to_string() << endl;
}

void MqttClient::delivery_complete(mqtt::delivery_token_ptr token) {
    cout << "\nDelivery complete for token: " << (token ? token->get_message_id() : -1) << endl;
}

void MqttClient::wait_for_messages() {
    cout << "Waiting for messages... (Press Ctrl+C to exit)" << endl;
    while (true) {
        this_thread::sleep_for(chrono::seconds(1));
    }
}
