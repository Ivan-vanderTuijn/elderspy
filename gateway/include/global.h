//
// Created by dorian on 10/27/24.
//

#ifndef GOBLAL_H
#define GOBLAL_H

const std::string GATEWAY_BROKER_ADDRESS("tcp://localhost:1883"); //Le port par défaut mqtt de NanoMQ est 1883
const std::string GATEWAY_CLIENT_ID("gateway");


const std::string BACKEND_BROKER_ADDRESS("tcp://localhost:1884");
const std::string BACKEND_SERVER_ADDRESS("http://localhost:8080");

#endif //GOBLAL_H
