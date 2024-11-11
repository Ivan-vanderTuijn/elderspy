# ElderSpy

## Usage
### Requirements
- Docker
- JDK 17
- Maven 3.9.6
- Kubernetes environment (optional)

### Running the project
1. Clone the repository
2. Build and start the backend containers
```shell
~/backend $ docker-compose up --build -d
```
3. Build and start the edge containers
```shell
~/gateway $ docker-compose up --build -d
```
4. Build and start the sensor container
```shell
~/sensors $ docker build -t elderspy-temp-sensor .
~/sensors $ docker run -d elderspy-temp-sensor
```
(optional) Kubernetes deployment 
```shell
~/backend/kubernetes $ kubectl apply -f ./namespace -f ./config-maps -f ./deployments -f ./services -f ./secrets -f ./hpa -n elderspy-ns
```
Accessing the following services:

InfluxDB UI:
```
http://localhost:8086/

username: admin
password: admin123
```
Grafana UI:
```
http://localhost:4000/

username: admin
password: admin123
```

## Architecture

## Documentation

### Telemetry client service API for Edge simulation

This API simulates sensor data normally published by the edge devices. When used, it will publish messages on the same topic that would be used by the edge device, simulating real telemetry data. The simulation allows testing and monitoring of data flow through RabbitMQ to the backend.

| **Endpoint**                         | **Method** | **Description**                                                                                                   | **Request Body**                                                                                                            |
|--------------------------------------|------------|-------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------|
| `/api/telemetry/send`                | `POST`     | Simulates a single sensor data entry with a timestamp, `edgeId`, `deviceId`, `measurement`, and `value`.         | ``` { "timestamp": "{{$isoTimestamp}}", "edgeId": "edge-001", "deviceId": "device-001", "measurement": "temperature", "value": 30.5 } ``` |
| `/{edgeId}/{deviceId}/start`         | `POST`     | Starts a continuous simulation of sensor data for the specified `edgeId` and `deviceId`.                         | None                                                                                                                        |
| `/{edgeId}/{deviceId}/stop`          | `POST`     | Stops the continuous simulation of sensor data for the specified `edgeId` and `deviceId`.                        | None                                                                                                                        |

**Notes**:
- **Simulation Trigger**: When these endpoints are called, they will simulate the behavior of an edge device by sending messages on the expected topic.
- **Message Flow**: The data sent through these endpoints will be forwarded via the RabbitMQ broker, making it accessible to the backend service for further processing.

---

## Risk analysis

## Code structure

```plaintext
elderspy/
├── backend/                               # Backend services and configurations
│   ├── grafana-provisioning/               # Grafana dashboard provisioning configurations and data sources (InfluxDB)
│   ├── kubernetes/                         # Kubernetes deployment configurations and manifests resources
│   ├── rabbitmq/                           # RabbitMQ configurations (plugins)
│   ├── services/                           # Microservices of the backend
│   │   ├── common/                          # Shared modules and utilities
│   │   ├── alerting-service/                # Service for alert notifications
│   │   ├── gsm-gateway/                     # GSM gateway service for GSM communication
│   │   ├── telemetry-analyser-service/      # Analyzes telemetry data
│   │   ├── telemetry-client/                # Client for sending telemetry data
│   │   └── telemetry-ingestion-service/     # Service to handle telemetry data ingestion
│   └── docker-compose.yml                  # Docker Compose file to run the backend services, InfluxDB, RabbitMQ and Grafana
├── gateway/                                # Gateway-related components (Edge)
│   ├── include/                             # Header files and other inclusions
│   ├── src/                                 # Source code for the gateway
│   │   ├── alert/                            # Alert handling logic
│   │   ├── mqtt-clients/                     # MQTT clients for message communication
│   │   └── config/                           # Configuration files for sensors
│   └── docker-compose.yml                   # Docker Compose file for gateway services
├── sensors/                                # Sensor-related configurations
│   └── Dockerfile                           # Docker setup for the sensor module
│   └── flows.json                           # Node-RED flow for the sensor module
└── README.md                               # Main project documentation file
```

## How to contribute




