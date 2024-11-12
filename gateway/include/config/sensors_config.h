#pragma once

#include <string>
#include <unordered_map>
#include <stdexcept>

// Define an enum to represent the types of sensors.
enum class SensorType {
    TEMPERATURE,
    HEART_RATE,
};

// Define an enum for alert severity levels.
enum class AlertSeverity {
    CRITICAL,
    SERIOUS,
    MINOR,
    ENVIRONMENTAL
};

// Define a struct to encapsulate sensor thresholds per severity level.
struct SensorThresholds {
    std::unordered_map<AlertSeverity, std::pair<int, int> > thresholds;
};

// Define a struct to encapsulate sensor configuration.
struct SensorConfig {
    SensorType type;
    std::string topic;
    SensorThresholds thresholds;
};

// Use unordered_map to store sensor configurations.
extern const std::unordered_map<SensorType, SensorConfig> sensorConfigs;

// Function declaration
SensorType getSensorTypeByTopic(const std::string &topic);
