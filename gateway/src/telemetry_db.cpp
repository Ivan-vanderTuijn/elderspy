#include "../include/telemetry_db.h"

using namespace std;

// Private constructor to enforce the singleton pattern
TelemetryDB::TelemetryDB(const string &path) : db(nullptr), db_path(path) {
    if (sqlite3_open(db_path.c_str(), &db) != SQLITE_OK) {
        cerr << "Cannot open database: " << sqlite3_errmsg(db) << endl;
    } else {
        cout << "Connected to database: " << db_path << endl;
    }
}

// Destructor to close the database connection
TelemetryDB::~TelemetryDB() {
    close();
}

// Method to get the singleton instance
TelemetryDB &TelemetryDB::getInstance() {
    static TelemetryDB instance(DATABASE_PATH);
    return instance;
}

// Static callback function for query execution
int TelemetryDB::callback(void *data, int argc, char **argv, char **azColName) {
    const char *callback_message = static_cast<const char *>(data);
    fprintf(stderr, "%s:\n", callback_message);

    for (int i = 0; i < argc; i++) {
        printf("%s = %s\n", azColName[i], argv[i] ? argv[i] : "NULL");
    }
    printf("\n");
    return 0;
}

// Helper method to execute queries
vector<vector<string> > TelemetryDB::executeQuery(const string &query) {
    vector<vector<string> > results;
    sqlite3_stmt *stmt;

    // Prepare the SQL statement
    if (sqlite3_prepare_v2(db, query.c_str(), -1, &stmt, nullptr) != SQLITE_OK) {
        cerr << "Failed to prepare statement: " << sqlite3_errmsg(db) << endl;
        return results; // Return empty results if failed
    }

    // Loop through the result set row by row
    while (sqlite3_step(stmt) == SQLITE_ROW) {
        vector<string> row;
        int num_cols = sqlite3_column_count(stmt);

        // Loop through each column in the row
        for (int i = 0; i < num_cols; i++) {
            const char *colValue = reinterpret_cast<const char *>(sqlite3_column_text(stmt, i));
            row.push_back(colValue ? colValue : "NULL");
        }

        results.push_back(row);
    }

    // Finalize the statement to clean up
    sqlite3_finalize(stmt);

    return results;
}

// Method to get house temperature data
vector<vector<string> > TelemetryDB::getHouseTemperature() {
    return executeQuery("SELECT payload FROM house_temperature");
}

// Method to get heart rate data
vector<vector<string> > TelemetryDB::getHeartRate() {
    return executeQuery("SELECT payload FROM heart_rate");
}


// Method to close the database connection
void TelemetryDB::close() {
    if (db) {
        sqlite3_close(db);
        db = nullptr;
        cout << "Database connection closed." << endl;
    }
}
