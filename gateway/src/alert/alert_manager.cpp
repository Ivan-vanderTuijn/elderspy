#include "alert/alert_manager.h"
#include <iostream>
#include <nlohmann/json.hpp>
#include <nlohmann/json_fwd.hpp>
#include <cpprest/http_client.h>  // Requires C++ REST SDK or a similar HTTP library

#include "config/global.h"
#include "mqtt_clients/mqtt_client_factory.h"

using namespace std;
using namespace nlohmann;

AlertManager::AlertManager(const std::string &backendUrl)
    : backendUrl(backendUrl),
      client(MqttClientFactory::createClient(MqttClientFactory::GATEWAY, [this](mqtt::const_message_ptr msg) {
          onMessage(msg);
      })) {
    client->subscribe("sensor/#", 1);
    cout << "[AlertManager] Subscribed to sensor topics." << endl; // Log subscription
}

void AlertManager::onMessage(mqtt::const_message_ptr msg) {
    string topic = msg->get_topic();
    cout << "[AlertManager] Message received on topic: " << topic << endl; // Log received message

    // Parse JSON payload
    json payload;
    try {
        payload = json::parse(msg->to_string());
        cout << "[AlertManager] Parsed JSON payload: " << payload.dump() << endl; // Log parsed payload
    } catch (const json::parse_error &e) {
        cerr << "[AlertManager] JSON parse error: " << e.what() << endl;
        return;
    }

    // Retrieve values from JSON
    string deviceId = payload["deviceId"].get<string>();
    float value = payload["value"].get<float>();
    cout << "[AlertManager] Device ID: " << deviceId << ", Value: " << value << endl; // Log device info

    // Generate the timestamp
    time_t now = time(nullptr);
    char timestamp[21]; // 20 + 1 for null terminator
    strftime(timestamp, sizeof(timestamp), "%Y-%m-%dT%H:%M:%SZ", localtime(&now));
    cout << "[AlertManager] Generated timestamp: " << timestamp << endl; // Log timestamp

    // Check sensor type and thresholds
    SensorType sensorType = getSensorTypeByTopic(topic);
    if (sensorConfigs.find(sensorType) == sensorConfigs.end()) {
        cerr << "[AlertManager] Unknown sensor type for topic: " << topic << endl;
        return;
    }
    cout << "[AlertManager] Sensor type determined: " << static_cast<int>(sensorType) << endl; // Log the sensor type


    const SensorConfig &config = sensorConfigs.at(sensorType);
    const SensorThresholds &thresholds = config.thresholds;

    // Check against thresholds for alerting
    AlertSeverity highestSeverity = AlertSeverity::ENVIRONMENTAL;
    bool alertSent = false;

    for (const auto &[severity, range] : thresholds.thresholds) {
        if (value > range.second || value < range.first) {
            if (severity < highestSeverity) {
                highestSeverity = severity;
            }
            alertSent = true;
        }
    }

    // Send only the highest priority alert if one was triggered
    if (alertSent) {
        cout << "[AlertManager] Value " << value << " exceeds thresholds for severity: " <<
                getSeverityString(highestSeverity) << endl;
        sendAlert(highestSeverity, deviceId, timestamp, std::to_string(value), topic);
    }

    cout << "[AlertManager] Threshold checks complete." << endl; // Log threshold checks
}

void AlertManager::sendAlert(const AlertSeverity &severity, const string &deviceId, const std::string &timestamp,
                             const std::string &value, const std::string topic) {
    cout << "[AlertManager] Preparing to send alert..." << endl; // Log alert preparation
    // Prepare the alert message
    cout << "COUCOU : " + backendUrl << endl;
    cout << "http://" + backendUrl + "/gsm-gateway/alert" << endl;
    web::http::client::http_client httpClient("http://" + backendUrl + "/gsm-gateway/alert");
    web::http::http_request request(web::http::methods::POST);

    // Build the JSON payload
    json jsonPayload;
    jsonPayload["severity"] = getSeverityString(severity);
    jsonPayload["edgeId"] = EDGE_ID; // Replace with actual edge ID if available
    jsonPayload["deviceId"] = deviceId;
    jsonPayload["timestamp"] = timestamp;
    //message = [Sévérité] : attention [topic] threshold exceed at [timestamp] with value [value]
    jsonPayload["message"] = "[" + getSeverityString(severity) + "] : " + topic + " threshold exceed at " + timestamp + " with value " + value;

    cout << "[AlertManager] Sending alert: " << jsonPayload.dump() << endl; // Log alert details

    request.set_body(jsonPayload.dump());
    request.headers().set_content_type("application/json");

    // Send the alert to the backend
    httpClient.request(request).then([](web::http::http_response response) {
        if (response.status_code() != web::http::status_codes::OK) {
            cerr << "[AlertManager] Alert failed to send: " << response.status_code() << endl;
        } else {
            cout << "[AlertManager] Alert sent successfully." << endl;
        }
    }).wait();
}

std::string AlertManager::getSeverityString(AlertSeverity severity) {
    switch (severity) {
        case AlertSeverity::CRITICAL: return "CRITICAL";
        case AlertSeverity::SERIOUS: return "SERIOUS";
        case AlertSeverity::MINOR: return "MINOR";
        case AlertSeverity::ENVIRONMENTAL: return "ENVIRONMENTAL";
        default: return "UNKNOWN";
    }
}
