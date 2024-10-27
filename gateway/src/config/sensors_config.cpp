#include "config/sensors_config.h"

// Define the sensor configurations
const std::unordered_map<SensorType, SensorConfig> sensorConfigs = {
    {SensorType::HOUSE_TEMPERATURE, {SensorType::HOUSE_TEMPERATURE, "sensor/house_temperature", {18, 25}}},
    {SensorType::HEART_RATE, {SensorType::HEART_RATE, "sensor/heart_rate", {60, 100}}}
};

// Define the function to get the sensor type by topic
SensorType getSensorTypeByTopic(const std::string& topic) {
    for (const auto& [key, config] : sensorConfigs) {
        if (config.topic == topic) {
            return config.type;
        }
    }
    throw std::invalid_argument("Invalid sensor topic");
}
