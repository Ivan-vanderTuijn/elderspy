#include <iostream>
#include <mqtt/async_client.h>
#include "mqtt_client.h"
#include "tcp_handler.h"

using namespace std;

const string SERVER_ADDRESS("tcp://localhost:1883"); //Le port par défaut mqtt de NanoMQ est 1883
const string CLIENT_ID("gateway");
const string TOPIC("house/1/temp/");

const int QOS = 1;
const string PAYLOAD("Payload de test");

class callback : public virtual mqtt::callback {
public:
    void connected(const string& cause) override {
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
    MqttClient mqttClient(SERVER_ADDRESS, CLIENT_ID);

    try {
        mqttClient.connect();
        mqttClient.subscribe(TOPIC, QOS);
        mqttClient.publish(TOPIC, PAYLOAD, QOS);

        mqttClient.wait_for_messages(); // Keep the client alive
    }
    catch (const mqtt::exception& exc) {
        cerr << "MQTT error: " << exc.what() << endl;
        return 1;
    }

    // // create an instance of your own tcp handler
    // TcpHandler myHandler;
    //
    // // address of the server
    // AMQP::Address address("amqp://guest:guest@localhost/vhost");
    //
    // // create a AMQP connection object
    // AMQP::TcpConnection connection(&myHandler, address);
    //
    // // and create a channel
    // AMQP::TcpChannel channel(&connection);
    //
    // // use the channel object to call the AMQP method you like
    // channel.declareExchange("my-exchange", AMQP::fanout);
    // channel.declareQueue("my-queue");
    // channel.bindQueue("my-exchange", "my-queue", "my-routing-key");

    return 0;
}

