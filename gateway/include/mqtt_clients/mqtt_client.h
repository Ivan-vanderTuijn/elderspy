#ifndef MQTTCLIENT_H
#define MQTTCLIENT_H

#include <mqtt/async_client.h>
#include <string>

class MqttClient : public virtual mqtt::callback {
public:
    MqttClient(const std::string& address, const std::string& clientId);

    void connect();

    void subscribe(const std::string& topic, int qos);

    void publish(const std::string& topic, const std::string& payload, int qos);

    void wait_for_messages();

private:
    mqtt::async_client client;

    void connected(const std::string& cause) override;

    void message_arrived(mqtt::const_message_ptr msg) override;

    void delivery_complete(mqtt::delivery_token_ptr token) override;
};

#endif // MQTTCLIENT_H
