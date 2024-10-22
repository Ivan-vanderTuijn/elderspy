# INSTALLER LA LIBRAIRIE MQTT

## Ajouter les dépendances suivantes :

```
sudo apt install libssl-dev
sudo apt install cmake
sudo apt-get install libsqlite3-dev
```


## Executer le script suivant :

```
git clone https://github.com/eclipse/paho.mqtt.cpp
cd paho.mqtt.cpp
git co v1.4.0
git submodule init
git submodule update
cmake -Bbuild -H. -DPAHO_WITH_MQTT_C=ON -DPAHO_BUILD_EXAMPLES=ON
sudo cmake --build build/ --target install
```