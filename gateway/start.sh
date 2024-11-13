#!/bin/bash

export BACKEND_IP=$(hostname -I | awk '{print $1}')

docker-compose up --build
