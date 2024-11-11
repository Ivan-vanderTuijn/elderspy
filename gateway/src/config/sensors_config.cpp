#include "config/sensors_config.h"

// Define the sensor configurations with severity-specific thresholds.
const std::unordered_map<SensorType, SensorConfig> sensorConfigs = {
    {
        SensorType::HOUSE_TEMPERATURE, {
            SensorType::HOUSE_TEMPERATURE, "sensor/house_temperature",
            {
                {
                    {AlertSeverity::CRITICAL, {6, 40}},
                    {AlertSeverity::SERIOUS, {12, 35}},
                    {AlertSeverity::MINOR, {18, 30}},
                }
            }
        }
    },
    {
        SensorType::HEART_RATE, {
            SensorType::HEART_RATE, "sensor/heart_rate",
            {
                {
                    {AlertSeverity::CRITICAL, {0, 140}},
                    {AlertSeverity::SERIOUS, {20, 130}},
                    {AlertSeverity::MINOR, {40, 100}},
                }
            }
        }
    }
};

// Define the function to get the sensor type by topic
SensorType getSensorTypeByTopic(const std::string &topic) {
    for (const auto &[key, config]: sensorConfigs) {
        if (config.topic == topic) {
            return config.type;
        }
    }
    throw std::invalid_argument("Invalid sensor topic: " + topic);
}
