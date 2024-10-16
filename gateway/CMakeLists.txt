cmake_minimum_required(VERSION 3.10)
project(Gateway)

# Set C++ standard
set(CMAKE_CXX_STANDARD 11)

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
add_executable(gateway ${SRC_FILES})

# Specify output directory for the executable
set_target_properties(gateway PROPERTIES
        RUNTIME_OUTPUT_DIRECTORY ${BUILD_DIR})

# Link libraries
target_link_libraries(gateway paho-mqttpp3 paho-mqtt3as amqpcpp pthread dl)
