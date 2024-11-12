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

### Backend

### Edge

### Edge-Backend communication

### Sensors

## Documentation

### Alerting via SMS
Currently, the alerting service is configured to send SMS alerts to a predefined phone number via an SMS API.  
The service is triggered when the telemetry analyser service detects an anomaly in the telemetry data, or when receiving an alert from the edge. 
The alerting service uses the `Free SMS API` to send SMS alerts.

If you want to use the SMS alerting service, you need to provide the following environment variables, for example via a `.env` file located in the `~/backend/services/gsm-gateway` directory:
- `FREE_SMS_API_USER`: The username for the Free SMS API
- `FREE_SMS_API_PASS`: The password for the Free SMS API

---

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

Voici un exemple de matrice de risque au format README. Cette matrice peut être utile pour identifier, évaluer et prioriser les risques en fonction de leur probabilité et de leur impact.

---

# Matrice de Risque

Voici la matrice de risques sur les principaux problèmes que nous avons identifiés. Pour rappel, les risques ssont classés en fonction de leur **probabilité** et de leur **impact**. 

---

|                  | **Impact Faible** | **Impact Modéré** | **Impact Important** | **Impact Grave**       | **Impact Catastrophique**       |
|------------------|-------------------|--------------------|-----------------------|-------------------------|----------------------------------|
| **Probabilité Très Faible** |               |                     |                        |                         |                                  |
| **Probabilité Faible**      |               |                     |🟡 **R7** (Intrusion AMQP/MQTT) | 🟠 **R9** (Saturation stockage SQLite) |                                  |
| **Probabilité Moyenne**     |               |🟠 **R6** (Mauvaise configuration des agents) | 🟠 **R3** (Fiabilité capteurs IoT) | 🔴**R5** (Panne Stream Processing) -  🔴**R4** (Performances Raspberry Pi)  | 🔴**R8** (Panne RabbitMQ)           |
| **Probabilité Élevée**      |               |  |  | 🔴**R2** (Indisponibilité des Services) | 🔴**R1** (Sécurité des données)      |

---

## Légende des couleurs

- 🔵 **Faible** : Risque mineur, nécessite peu ou pas de mesures d'atténuation.
- 🟡 **Modéré** : Risque modéré, surveillé et géré si nécessaire.
- 🟠 **Significatif** : Risque important, actions d'atténuation à considérer.
- 🔴 **Critique** : Risque majeur, nécessite des mesures immédiates et des plans de gestion.


---
1. **Risque de sécurité des données (R1)**
   - Ce risque est critique en raison de la nature sensible des données médicales échangées. Une fuite de ces informations pourrait avoir des répercussions énormes. Étant donné le nombre d'intermédiaire parmis lequel circule ces données ainsi que la nature de ces données. Ce risque est classé comme "Important" et "Catastrophique" il est donc primordial de sécuriser ces données.

2. **Risque d’indisponibilité de certains services (R2)**
   - La perte de connectivité à certains services est aussi un risque à prendre en compte, notre système utilisant énormément de micro-services, il est important de bien gérer pour chaucun les cas il n'y aura plus de contact avec l'exterieur. La probabilité de ce risque est élevée, et l'impact est grave car il interromprait le fonctionnement normal du système. Quand bien même des systèmes de fonctionnement offline soient mis en placess, leurs fonctionnement resterait limité.

3. **Risque de fiabilité des capteurs IoT (R3)**
   - Les capteurs qui sont présents dans les domiciles que elderspy mets à disposition, bien que très performants peuvent tomber en panne ou se déconnecter. L'impact peut varier (en fonction du type de capteur) nous placerons ici le risque le plus élevé parmis tous ces capteurs : Important. En effet, certains capteurs peuvent tomber en panne sans grande incidence pour le patient (Temperature de l'environnement / Qualité de l'air intérieur). Cependant, certains capteurs surveillant les constantes vitales du patient se doivent d'avoir une disponibilité constante. La moindre déconnexion peut mener à une perte importante de données (manque de données pour reconnaitre des patterns sur les modèles de maladies cardiaques par exemple)

4. **Risque de performances du Raspberry Pi (R4)**
   - Le Raspberry Pi est la passerelle principale, en fonction du nombre d'appareils à traiter, des problèmes de performance et, entre autre de surchauffe peuvent survenirs. Cela conduirait à une indisponibilité temporaire de la gateway, ce qui serait grave.

5. **Risque de panne du service de date processing (R5)**
   - Le service de traitement de flux est critique pour l’analyse en temps réel. Sa panne ralentirait le traitement et pourrait empêcher la détection d’événements. La probabilité est moyenne, car bien que le Raspberry Pi soit limité en ressources, des redémarrages automatiques et des protections peuvent limiter les interruptions.

6. **Risque de Mauvaise configuration des différents agents (R6)**
   - La mauvaise configuration des agents peut causer des problèmes temporaires, mais les effets sont en général récupérables avec des ajustements ou un redémarrage. La probabilité est moyenne en raison de la complexité des configurations requises pour l'intégration de divers services.

7. **Risque d’intrusion via les services AMQP et MQTT (R7)**
   - Bien que ce risque ait un impact important, la probabilité d'une intrusion est réduite si les bonnes pratiques de sécurité (chiffrement et authentification) sont appliquées.

8. **Risque de panne du RabbitMQ ou des services de messagerie (R8)**
   - RabbitMQ est central dans cette architecture pour la gestion des messages, et sa non-disponibilitée causerait des perturbations majeures. La probabilité est moyenne, car bien que RabbitMQ soit fiable, il peut être soumis à des interruptions si la charge est élevée ou si une maintenance non planifiée se produit.

9. **Risque de manque de capacité de stockage (SQLite sur le Pi) (R9)**
    - La saturation du stockage SQLite est possible si les données ne sont pas régulièrement archivées ou supprimées, ou encore, si un accès à Internet coupé de manière trop prolongé. Bien que l'impact soit grave (perte de données), la probabilité est faible si un système de gestion de données efficace est mis en place. Même en cas de coupure d'accès internet, le système doit être en parfaite capacité de fonctionner de manière autonome.


---

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

We welcome contributions to the **elderspy** project! Here’s how you can get involved:

### 1. Fork the Repository
- Start by forking the repository to your own GitHub account.

### 2. Clone the Repository
- Clone your forked repository to your local machine:
```bash
git clone https://github.com/your-username/elderspy.git
```

### 3. Set Up the Environment
- Ensure you have the necessary dependencies installed, including Docker and any specific tools listed in the README.md.

### 4. Make Changes
- Make your changes to the codebase. Ensure that your changes are in line with the project’s guidelines and coding standards.
- Test your changes locally to ensure they function as expected.
- Add tests for your changes if applicable.
- Update the documentation to reflect your changes if necessary.
- Commit your changes and push them to your forked repository.

### 5. Create a Pull Request
- Create a pull request to the main repository.
- Ensure your pull request description clearly explains the changes you’ve made and the reason for the changes.

### 6. Review and Collaborate
- Participate in the code review process by responding to feedback and making any necessary changes to your pull request.
