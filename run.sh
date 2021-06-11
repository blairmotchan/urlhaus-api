#! /bin/bash

mvn spring-boot:build-image -DskipTests && docker-compose up