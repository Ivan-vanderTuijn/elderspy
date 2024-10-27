//
// Created by dorian on 10/27/24.
//

#ifndef TELEMETRY_DATA_H
#define TELEMETRY_DATA_H

#include <string>

struct TelemetryData {
    std::string timestamp;
    std::string edgeId;
    std::string deviceId;
    std::string measurement;
    double value;

    // Constructor
    TelemetryData(const std::string &ts, const std::string &edge, const std::string &device,
                  const std::string &meas, double val)
        : timestamp(ts), edgeId(edge), deviceId(device), measurement(meas), value(val) {
    }
};


#endif //TELEMETRY_DATA_H
