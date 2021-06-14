#! /bin/bash

if [ ! -e src/main/resources/csv.txt ]; then
  unzip src/main/resources/csv.txt.zip -d src/main/resources
fi
mvn clean install -DskipTests
mvn spring-boot:build-image -DskipTests
docker-compose up