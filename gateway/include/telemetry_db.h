#ifndef TELEMETRY_DB_H
#define TELEMETRY_DB_H

#include <sqlite3.h>
#include <string>
#include <vector>
#include <iostream>

const std::string DATABASE_PATH("../../data/telemetry.db");
const std::string TABLE_NAME("telemetry");

class TelemetryDB {
private:
    sqlite3 *db;
    std::string db_path;

    // Private constructor for singleton pattern
    explicit TelemetryDB(const std::string &path);

    // Prevent copying
    TelemetryDB(const TelemetryDB &) = delete;

    TelemetryDB &operator=(const TelemetryDB &) = delete;

    // Static callback function for query execution
    static int callback(void *data, int argc, char **argv, char **azColName);

public:
    // Destructor to close the database connection
    ~TelemetryDB();

    // Static method to get the singleton instance
    static TelemetryDB &getInstance();

    // Method to query the telemetry table
    std::vector<std::vector<std::string> > getAllTelemetryData(const std::string &table_name);

    // Close the database
    void close();
};

#endif // TELEMETRY_DB_H
