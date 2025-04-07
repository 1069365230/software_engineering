#!/bin/bash

# package the application (build the jar)
echo "Building the Jar..."
mvn package -Dmaven.test.skip

# build the container image
echo "Building the Docker image..."
docker build -t recommender-service .

# run unit-tests
echo "Executing Unit-Tests..."
mvn test -Dtest=\!integration.*Test

# run integration-tests
echo "Executing Integration-Tests..."
mvn test -Dtest=integration.*Test

