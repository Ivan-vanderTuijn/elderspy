#include <iostream>
#include <csignal>
#include <chrono>

#include "sensors_data_storer.h"
#include "mqtt_clients/gateway_mqtt_client.h"
#include "alert/alert_manager.h"
#include "config/global.h"

volatile std::sig_atomic_t stop;

void handle_signal(int signal) {
    stop = 1;
}

int main() {
    MqttClient mqttClient(GATEWAY_BROKER_ADDRESS, GATEWAY_CLIENT_ID);
    // AlertManager alertManager = AlertManager("http://backend_url:8080");
    SensorsDataStorer sensorsDataStorer = SensorsDataStorer(mqttClient);

    // Set up signal handling for clean exit
    std::signal(SIGINT, handle_signal); // Handle Ctrl+C

    try {
        std::cout << "IoT Client running. Press Ctrl+C to exit." << std::endl;

        while (!stop) {
            std::this_thread::sleep_for(std::chrono::seconds(1));
        }
    } catch (const std::exception &e) {
        std::cerr << "Error: " << e.what() << std::endl;
    }

    std::cout << "Shutting down..." << std::endl;

    return 0;
}
