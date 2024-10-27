#ifndef SENSORS_CONFIG_H
#define SENSORS_CONFIG_H

#include <string>
#include <unordered_map>
#include <stdexcept> // Include this header for std::invalid_argument

// Define an enum to represent the types of sensors.
enum class SensorType {
    HOUSE_TEMPERATURE,
    HEART_RATE,
};

// Define a struct to encapsulate sensor thresholds.
struct SensorThresholds {
    int min;
    int max;
};

// Define a struct to encapsulate sensor configuration.
struct SensorConfig {
    SensorType type;
    std::string topic;
    SensorThresholds thresholds;
};

// Use unordered_map to store sensor configurations.
extern const std::unordered_map<SensorType, SensorConfig> sensorConfigs; // Declare it as extern

// Function declaration
SensorType getSensorTypeByTopic(const std::string &topic); // Declare the function

#endif // SENSORS_CONFIG_H
