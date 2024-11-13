# ElderSpy

## Utilisation
### Prérequis
- Docker
- JDK 17
- Maven 3.9.6
- Kubernetes environment (optional)

### Exécution du projet
1. Cloner le repository
2. Construire et démarrer les conteneurs backend
```shell
~/backend $ docker-compose up --build -d
```
3. Construire et démarrer les conteneurs edge
```shell
~/gateway $ docker-compose up --build -d
```
(optionnel) Déploiement Kubernetes (Work In Progress)
```shell
~/backend/kubernetes $ kubectl apply -f ./namespace -f ./config-maps -f ./deployments -f ./services -f ./secrets -f ./hpa -n elderspy-ns
```
Accéder aux services suivants :

NodeRed (simulation capteurs):
```
http://localhost:1880/
```

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

### Aperçu
![architecture overview](/docs/resource/full_archi.png)

### Communication Edge-Backend
![architecture overview](/docs/resource/edge_backend_comm.png)

### Backend
![architecture overview](/docs/resource/only_backend.png)

### Edge et Assistant
![architecture overview](/docs/resource/assistant_and_edge.png)

### Edge (Data processing)
![architecture overview](/docs/resource/data_processing_edge.png)

## Documentation

### Alerting via SMS
Actuellement, le service d'alerte est configuré pour envoyer des alertes SMS à un numéro de téléphone prédéfini via une API SMS.
Le service est déclenché lorsque le service d'analyse de télémétrie détecte une anomalie dans les données ou lorsqu'une alerte est reçue du edge.
Le service d'alerte utilise l'`API Free SMS` pour envoyer les alertes SMS.

Si vous souhaitez utiliser le service d'alerte SMS, vous devez fournir les variables d'environnement suivantes, par exemple via un fichier`.env` situé dans le répertoire `~/backend/services/gsm-gateway`:
- `FREE_SMS_API_USER`: Nom d'utilisateur pour l'API Free SMS
- `FREE_SMS_API_PASS`: Mot de passe pour l'API Free SMS

---

### Telemetry client service API for Edge simulation

Cette API simule les données des capteurs publiées par les dispositifs edge. Lorsqu'elle est utilisée, elle publie des messages sur le même sujet que celui du edge, simulant ainsi des données de télémétrie réelles. La simulation permet de tester et de surveiller le flux de données via RabbitMQ jusqu'au backend.  

| **Endpoint**                         | **Method** | **Description**                                                                                                   | **Request Body**                                                                                                            |
|--------------------------------------|------------|-------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------|
| `/api/telemetry/send`                | `POST`     | Simulates a single sensor data entry with a timestamp, `edgeId`, `deviceId`, `measurement`, and `value`.         | ``` { "timestamp": "{{$isoTimestamp}}", "edgeId": "edge-001", "deviceId": "device-001", "measurement": "temperature", "value": 30.5 } ``` |
| `/{edgeId}/{deviceId}/start`         | `POST`     | Starts a continuous simulation of sensor data for the specified `edgeId` and `deviceId`.                         | None                                                                                                                        |
| `/{edgeId}/{deviceId}/stop`          | `POST`     | Stops the continuous simulation of sensor data for the specified `edgeId` and `deviceId`.                        | None                                                                                                                        |

**Notes**:
- **Simulation Trigger**: Déclenchement de la simulation : Ces endpoints simulent le comportement d'un dispositif edge en envoyant des messages sur le sujet attendu.
- **Message Flow**: Les données envoyées via ces endpoints seront transmises via le broker RabbitMQ, ce qui les rend accessibles au service backend pour un traitement ultérieur.

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
├── backend/                               # Services backend et configurations
│   ├── grafana-provisioning/               # Configuration de provisionnement des tableaux de bord Grafana et sources de données (InfluxDB)
│   ├── kubernetes/                         # Configurations de déploiement Kubernetes et ressources manifests
│   ├── rabbitmq/                           # Configurations de RabbitMQ (plugins)
│   ├── services/                           # Microservices du backend
│   │   ├── common/                          # Modules partagés et utilitaires
│   │   ├── alerting-service/                # Service pour les notifications d'alerte
│   │   ├── gsm-gateway/                     # Service de passerelle GSM pour la communication GSM
│   │   ├── telemetry-analyser-service/      # Analyse les données de télémétrie
│   │   ├── telemetry-client/                # Client pour l'envoi des données de télémétrie
│   │   └── telemetry-ingestion-service/     # Service pour gérer l'ingestion des données de télémétrie
│   └── docker-compose.yml                  # Fichier Docker Compose pour exécuter les services backend, InfluxDB, RabbitMQ et Grafana
├── gateway/                                # Composants liés à la passerelle (Edge)
│   ├── include/                             # Fichiers d'en-tête et autres inclusions
│   ├── src/                                 # Code source pour la passerelle
│   │   ├── alert/                            # Logique de gestion des alertes
│   │   ├── mqtt-clients/                     # Clients MQTT pour la communication de messages
│   │   └── config/                           # Fichiers de configuration pour les capteurs
│   └── docker-compose.yml                   # Fichier Docker Compose pour les services de la passerelle
├── sensors/                                # Configurations des capteurs
│   └── Dockerfile                           # Configuration Docker pour le module de capteur
│   └── flows.json                           # Flux Node-RED pour le module de capteur
└── README.md                               # Fichier principal de documentation du projet
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
