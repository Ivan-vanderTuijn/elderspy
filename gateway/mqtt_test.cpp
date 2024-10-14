#include <iostream>
#include <mqtt/async_client.h>

using namespace std;

const string SERVER_ADDRESS("tcp://localhost:1883"); //Le port par défaut mqtt de NanoMQ est 1883
const string CLIENT_ID("sampleClient");
const string TOPIC("test/topic");

const int QOS = 1;
const string PAYLOAD("Hello NanoMQ from C++");

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
    mqtt::async_client client(SERVER_ADDRESS, CLIENT_ID);
    callback cb;
    client.set_callback(cb);

    mqtt::connect_options connOpts;
    connOpts.set_clean_session(true);

    try {
        // Connect to NanoMQ
        cout << "Connecting to the NanoMQ server..." << endl;
        client.connect(connOpts)->wait();
        cout << "Connected to NanoMQ!" << endl;

        // Subscribe to a topic
        cout << "Subscribing to topic: " << TOPIC << endl;
        client.subscribe(TOPIC, QOS);

        // Publish a message
        cout << "Publishing message to NanoMQ: " << PAYLOAD << endl;
        mqtt::message_ptr pubmsg = mqtt::make_message(TOPIC, PAYLOAD);
        pubmsg->set_qos(QOS);
        client.publish(pubmsg)->wait();

        // Keep the client alive for a while to receive messages
        this_thread::sleep_for(chrono::seconds(5));

        // Disconnect
        cout << "Disconnecting from NanoMQ..." << endl;
        client.disconnect()->wait();
        cout << "Disconnected!" << endl;
    }
    catch (const mqtt::exception& exc) {
        cerr << "Error: " << exc.what() << endl;
        return 1;
    }

    return 0;
}

