
#include <mqtt_clients/mqtt_client.h>
#include <nlohmann/json.hpp>
#include "config/global.h"

using namespace std;

MqttClient::MqttClient(const string &address, const string &clientId, const string &username,
                       const string &password)
    : client(address, clientId) {
    client.set_callback(*this);
    connect(username, password);
}

MqttClient::~MqttClient() {
    disconnect();
}

void MqttClient::connect(const string &username, const string &password) {
    mqtt::connect_options connOpts;
    connOpts.set_clean_session(true);

    // Set the username and password only if provided
    if (!username.empty()) {
        connOpts.set_user_name(username);
    }
    if (!password.empty()) {
        connOpts.set_password(password);
    }

    try {
        cout << "Connecting to the broker server..." << endl;
        cout << "Address: " << client.get_server_uri() << endl;
        cout << "Client ID: " << client.get_client_id() << endl;
        client.connect(connOpts)->wait();
        cout << "Connected to broker!" << endl;
    } catch (const mqtt::exception &exc) {
        cerr << "Error: " << exc.what() << endl;
        throw;
    }
}

void MqttClient::disconnect() {
    try {
        client.disconnect()->wait();
        cout << "Disconnected from broker." << endl;
    } catch (const mqtt::exception &exc) {
        cerr << "Error during disconnection: " << exc.what() << endl;
    }
}

void MqttClient::subscribe(const string &topic, int qos) {
    try {
        cout << "Subscribing to topic: " << topic << endl;
        client.subscribe(topic, qos)->wait();
    } catch (const mqtt::exception &exc) {
        cerr << "Error during subscription: " << exc.what() << endl;
    }
}

void MqttClient::publish(const string &topic, const string &payload, int qos) {
    try {
        cout << "Publishing message to broker: " << payload << endl;
        mqtt::message_ptr pubmsg = mqtt::make_message(topic, payload);
        pubmsg->set_qos(qos);
        client.publish(pubmsg);
    } catch (const mqtt::exception &exc) {
        cerr << "Error during publish: " << exc.what() << endl;
    }
}

int MqttClient::add_message_callback(MessageCallback cb) {
    lock_guard<mutex> lock(callback_mutex);
    int id = next_callback_id++;
    message_callbacks[id] = move(cb);
    return id;
}

void MqttClient::update_message_callback(int id, MessageCallback cb) {
    lock_guard<mutex> lock(callback_mutex);
    message_callbacks[id] = move(cb);
}

void MqttClient::remove_message_callback(int id) {
    lock_guard<mutex> lock(callback_mutex);
    message_callbacks.erase(id);
}


void MqttClient::message_arrived(mqtt::const_message_ptr msg) {
    string topic = msg->get_topic();
    string payload = msg->to_string();

    cout << "\nMessage arrived on topic: " << topic << endl;
    cout << "Payload: " << payload << endl;

    // Call registered message callbacks
    lock_guard<mutex> lock(callback_mutex);
    for (const auto &[id, cb]: message_callbacks) {
        if (cb) {
            cb(msg);
        }
    }
}

void MqttClient::delivery_complete(mqtt::delivery_token_ptr token) {
    cout << "\nDelivery complete for token: " << (token ? token->get_message_id() : -1) << endl;
}
