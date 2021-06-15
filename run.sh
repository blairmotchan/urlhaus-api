#! /bin/bash

if [ ! -e src/main/resources/csv_ip.txt ]; then
  unzip src/main/resources/csv_ip.txt.zip -d src/main/resources
fi
mvn clean install -DskipTests
mvn spring-boot:build-image -DskipTests
docker-compose up