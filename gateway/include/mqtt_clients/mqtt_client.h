#ifndef MQTTCLIENT_H
#define MQTTCLIENT_H

#include <mqtt/async_client.h>
#include <string>
#include <functional>
#include <unordered_map>
#include <mutex>
#include <iostream>

class MqttClient : public virtual mqtt::callback {
public:
    using MessageCallback = std::function<void(mqtt::const_message_ptr)>;

    MqttClient(const std::string &address, const std::string &clientId);

    ~MqttClient();

    void subscribe(const std::string &topic, int qos);

    void publish(const std::string &topic, const std::string &payload, int qos);

    int add_message_callback(MessageCallback cb);

    void update_message_callback(int id, MessageCallback cb);

    void remove_message_callback(int id);

private:
    void connect();

    void disconnect();


    mqtt::async_client client;
    std::unordered_map<int, MessageCallback> message_callbacks;
    std::mutex callback_mutex;
    int next_callback_id = 0;


    void message_arrived(mqtt::const_message_ptr msg) override;

    void delivery_complete(mqtt::delivery_token_ptr token) override;
};

#endif // MQTTCLIENT_H
