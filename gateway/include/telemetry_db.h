#ifndef TELEMETRY_DB_H
#define TELEMETRY_DB_H

#include <sqlite3.h>
#include <string>
#include <vector>
#include <iostream>

const std::string DATABASE_PATH("../../telemetry.db");
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

    void createTablesIfNotExist() const;

public:
    // Destructor to close the database connection
    ~TelemetryDB();

    // Static method to get the singleton instance
    static TelemetryDB &getInstance();

    // Helper method to execute a SQL query
    std::vector<std::vector<std::string> > executeQuery(const std::string &query);

    // Method to query specific telemetry data
    std::vector<std::vector<std::string> > getHouseTemperature();

    std::vector<std::vector<std::string> > getHeartRate();

    // Close the database
    void close();
};

#endif // TELEMETRY_DB_H
