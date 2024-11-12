# Use an appropriate base image
FROM ubuntu:20.04

# Set timezone configuration to avoid tzdata prompt
ENV DEBIAN_FRONTEND=noninteractive
ENV TZ=Etc/UTC

# Install dependencies
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        libssl-dev cmake libsqlite3-dev libcpprest-dev git tzdata build-essential nlohmann-json3-dev && \
    rm -rf /var/lib/apt/lists/*

# Disable SSL verification for git temporarily
RUN git config --global http.sslVerify "false"

# Clone, build, and install Paho MQTT library
RUN git clone https://github.com/eclipse/paho.mqtt.cpp.git && \
    cd paho.mqtt.cpp && \
    git checkout v1.4.0 && \
    git submodule init && \
    git submodule update && \
    cmake -Bbuild -H. -DPAHO_WITH_MQTT_C=ON -DPAHO_BUILD_EXAMPLES=ON && \
    cmake --build build/ --target install && \
    cd .. && rm -rf paho.mqtt.cpp

# Re-enable SSL verification (optional, but recommended for security)
RUN git config --global http.sslVerify "true"

# Set the working directory
WORKDIR /app

# Copy source files to the container
COPY . .

# Build the project
RUN cmake -Bbuild -H. && \
    cmake --build build --target gateway && \
    # Remove any unnecessary files after build to keep image size smaller
    apt-get remove -y git cmake build-essential && \
    apt-get autoremove -y && \
    rm -rf /var/lib/apt/lists/*

# Expose any required ports here (if applicable)
# EXPOSE <port>

# Set the entrypoint to run the application
ENTRYPOINT ["./build/build/gateway"]