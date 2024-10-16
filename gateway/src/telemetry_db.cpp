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

// Method to get the whole result of a query
vector<vector<string> > TelemetryDB::getAllTelemetryData(const string &table_name) {
    vector<vector<string> > results;
    sqlite3_stmt *stmt;
    string sqlQuery = "SELECT * FROM " + table_name;

    // Prepare the SQL statement
    if (sqlite3_prepare_v2(db, sqlQuery.c_str(), -1, &stmt, nullptr) != SQLITE_OK) {
        cerr << "Failed to prepare statement: " << sqlite3_errmsg(db) << endl;
        return results; // Return empty results if failed
    }

    // Loop through the result set row by row
    while (sqlite3_step(stmt) == SQLITE_ROW) {
        vector<string> row;

        // Get the number of columns in the current row
        int num_cols = sqlite3_column_count(stmt);

        // Loop through each column in the row
        for (int i = 0; i < num_cols; i++) {
            // Get the column value as a string
            const char *colValue = reinterpret_cast<const char *>(sqlite3_column_text(stmt, i));
            row.push_back(colValue ? colValue : "NULL");
        }

        // Add the row to the results vector
        results.push_back(row);
    }

    // Finalize the statement to clean up
    sqlite3_finalize(stmt);

    return results;
}


// Method to close the database connection
void TelemetryDB::close() {
    if (db) {
        sqlite3_close(db);
        db = nullptr;
        cout << "Database connection closed." << endl;
    }
}
