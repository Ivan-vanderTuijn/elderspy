//
// Created by dorian on 10/27/24.
//

#ifndef GOBLAL_H
#define GOBLAL_H
#include <string>
#include <cstdlib>


const std::string EDGE_ID("edge-001");
const std::string GATEWAY_BROKER_ADDRESS("tcp://nanomq:1883");


const ::std::string BACKEND_IP(std::getenv("BACKEND_IP"));

const std::string BACKEND_BROKER_ADDRESS("tcp://" + BACKEND_IP + ":1883");
const std::string BACKEND_TELEMETRY_TOPIC("telemetry-topic");
const std::string RABBITMQ_DEFAULT_USER("admin");
const std::string RABBITMQ_DEFAULT_PASS("admin123");


const std::string BACKEND_SERVER_ADDRESS("http://" + BACKEND_IP + ":9904");

#endif //GOBLAL_H
