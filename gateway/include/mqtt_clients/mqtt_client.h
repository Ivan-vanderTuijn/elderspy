#ifndef MQTTCLIENT_H
#define MQTTCLIENT_H

#include <mqtt/async_client.h>
#include <string>
#include <functional>
#include <mutex>
#include <iostream>

class MqttClient : public virtual mqtt::callback {
public:
    using MessageCallback = std::function<void(mqtt::const_message_ptr)>;

    MqttClient(const std::string &address, const std::string &clientId, MessageCallback messageCallback = nullptr,
               const std::string &username = "",
               const std::string &password = "");

    ~MqttClient();

    void subscribe(const std::string &topic, int qos);

    void publish(const std::string &topic, const std::string &payload, int qos);

private:
    void connect(const std::string &username, const std::string &password);

    void disconnect();

    mqtt::async_client client;
    MessageCallback message_callback;

    void message_arrived(mqtt::const_message_ptr msg) override;

    void delivery_complete(mqtt::delivery_token_ptr token) override;
};

#endif // MQTTCLIENT_H
