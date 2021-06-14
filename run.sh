#! /bin/bash

mvn clean install -DskipTests
cd api
mvn spring-boot:build-image -DskipTests
cd ../
docker-compose up