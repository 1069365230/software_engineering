#!/bin/bash

# build all jars
echo "Building the Jars..."
mvn clean package -Dmaven.test.skip --projects !edu.ems:frontend

# build e2e-test docker image
echo "Building the E2E-Docker image..."
cd ./end-to-end
docker build -t end-to-end-tests -f Dockerfile .

# start the system
cd ../
echo "Starting all system components..."
docker-compose up --build -d api-gateway login-service feedback-service analyticsandreport-service event-inventory-service marktag-service export-service attendance-service recommender-service notification-service

# wait until all services are initialized
echo "Make sure that all services have started..."
sleep 100s

# run the e2e test
echo "Executing E2E-Test..."
docker run --network implementation_default end-to-end-tests

# stop and remove containers, networks,...
echo "Stopping containers..."
docker-compose down
