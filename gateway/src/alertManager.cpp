#include "alertManager.h"
#include <iostream>
#include <stdexcept>
#include <cpprest/http_client.h>
#include <cpprest/json.h>

using namespace web;
using namespace web::http;
using namespace web::http::client;
using namespace std;

// Constructor
AlertManager::AlertManager(const string &backendUrl)
    : backendUrl(backendUrl) {
}

// Handle incoming sensor data
void AlertManager::handleSensorData(SensorType type, const string &payload) {
    // Parse the payload (assuming it contains a numeric value as a string)
    double value;
    try {
        value = stod(payload);
    } catch (const invalid_argument &e) {
        cerr << "Invalid payload: " << payload << endl;
        return;
    } catch (const out_of_range &e) {
        cerr << "Payload value out of range: " << payload << endl;
        return;
    }

    // Check if the value is above the threshold
    if (isAboveThreshold(type, value)) {
        sendAlertToBackend(type, value);
    }
}

// Check if the value is above the configured thresholds
bool AlertManager::isAboveThreshold(SensorType type, double value) {
    auto it = sensorConfigs.find(type);
    if (it == sensorConfigs.end()) {
        cerr << "Sensor type not found: " << static_cast<int>(type) << endl;
        return false; // Return false if the type is not found
    }
    const SensorThresholds &thresholds = it->second.thresholds;
    return value < thresholds.min || value > thresholds.max;
}

// Send an alert to the backend
void AlertManager::sendAlertToBackend(SensorType type, double value) {
    // // Create a JSON object to hold the alert data
    // json::value alertData;
    // alertData[U("sensor_type")] = json::value(static_cast<int>(type));
    // alertData[U("value")] = json::value(value);
    //
    // // Create an HTTP client
    // http_client client(U(backendUrl));
    //
    // // Send a POST request to the backend
    // client.request(methods::POST, U("/alerts"), alertData.serialize(), U("application/json"))
    //         .then([](http_response response) {
    //             if (response.status_code() == status_codes::OK) {
    //                 cout << "Alert sent successfully." << endl;
    //             } else {
    //                 cerr << "Failed to send alert. Status code: " << response.status_code() << endl;
    //             }
    //         })
    //         .wait(); // Wait for the response (blocking call)
}
