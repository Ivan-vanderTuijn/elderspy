cmake_minimum_required(VERSION 3.10)
project(Gateway)

# Set C++ standard
set(CMAKE_CXX_STANDARD 17)

# Define include directories
include_directories(include)

# Set compiler flags
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wall")

# Specify source and output directories
set(SRC_DIR src)
set(BUILD_DIR ${CMAKE_BINARY_DIR}/build)

# Find all source files in the SRC_DIR
file(GLOB SRC_FILES "${SRC_DIR}/*.cpp")

# Set the target output executable name (without directory)
add_executable(gateway ${SRC_FILES}
        include/alert/alert_manager.h
        include/config/global.h
        include/config/sensors_config.h
        include/mqtt_clients/backend_mqtt_client.h
        include/mqtt_clients/gateway_mqtt_client.h
        include/structs/telemetry_data.h
        include/telemetry_db.h
        src/alert/alert_manager.cpp
        src/config/sensors_config.cpp
        src/mqtt_clients/backend_mqtt_client.cpp
        src/mqtt_clients/gateway_mqtt_client.cpp
        src/mqtt_clients/mqtt_client.cpp
)

# Specify output directory for the executable
set_target_properties(gateway PROPERTIES
        RUNTIME_OUTPUT_DIRECTORY ${BUILD_DIR})

# Find OpenSSL
find_package(OpenSSL REQUIRED)

# Find cpprestsdk
find_package(cpprestsdk REQUIRED)

# Link libraries
target_link_libraries(gateway
        paho-mqttpp3
        paho-mqtt3as
        amqpcpp
        pthread
        dl
        sqlite3
        cpprestsdk::cpprest
        OpenSSL::SSL
        OpenSSL::Crypto) # Link OpenSSL libraries
