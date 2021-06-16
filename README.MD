[![Build Status](https://travis-ci.com/blairmotchan/urlhaus-api.svg?branch=main)](https://travis-ci.com/blairmotchan/urlhaus-api)

# Malware Url Searcher

We all know to stay away from "sketchy" websites because they are full of vulnerabilities, exploits, and viruses. 
But, what if you want to know what type of "sketchy" websites actually exist, what type of threat they pose, and how far away the closest website is?

Answering those questions is the purpose of this API. It provides programmatic access to current live "sketcy" websites. 
It provides some rudimentary searching of them along with the ability to find the closest physically located website by latitude and longitude. 

## Requirements to run
* Java 16 jdk
* Docker

## How to run
Simply run `./run.sh` from the command line.

The run bash script performs the following actions:
* Unzips the data that will be loaded by the application
* Creates a docker image of the API
* Runs `docker-compose up` to start a Postgres docker image with Postgis

You can verify the health of your API at http://localhost:8080/actuator/health

## How to access API Docs
A static version of the OpenAPI v3 docs can be found in the file `open-api.json`.

A much nicer and interactive Swagger version of the docs can be found after running `./run.sh` and navigating to http://localhost:8080/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config

## How to test and how to run Integration tests
Running `./mvnw clean test` will test out the application including integration tests of the API

## Requirements to recompile databases
The precompiled database/csv of Malware URLs comes from https://urlhaus.abuse.ch/api/ and that list is updated daily.
Additionally, the precompiled database/csv has IP and geography data loaded from https://dev.maxmind.com/geoip/geolite2-free-geolocation-data

If you want to use the latest data then these steps need to be followed:
1) Download https://urlhaus.abuse.ch/downloads/csv/
2) Unzip the file and move it to your src/main/resources directory
3) Sign up for a maxmind account and download the latest city database and save it to src/main/resources
4) Verify all of your input and output variables in `application.yml` and `application-live.yml` to make sure all the file names are correct
5) Change `recompile-databases: false` to `recompile-databases: true` in `application.yml` 
6) Run the spring application, but not as a docker image/container so that it has access to your file system and can overwrite the necessary files

## Acknowledgements

This product includes GeoLite2 data created by MaxMind, available from http://www.maxmind.com

https://urlhaus.abuse.ch/api/

## Next Steps
* Better error handling
* Build static documentation during build
* Learn more about SRIDs to provide more accurate distances
* Lookup IP information for URLs that are not IPs
* Ignore comment lines in the urlhaus data.
* Make it easier to recompile databases