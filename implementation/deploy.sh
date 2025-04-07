#!/bin/bash
# build all jars
echo "Building the Jars..."
mvn clean package -Dmaven.test.skip --projects !edu.ems:frontend

# Start the whole system, including the frontend which can be accessed on localhost:4200
echo "Starting the system and all its component, the frontend can be accessed on localhost:4200 after startup..."
docker-compose up --build
