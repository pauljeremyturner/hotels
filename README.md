# Hotel Booking System

This is an implementation exercise  to give the project team a short but useful insight into design and coding approach/style used by the aspiring candidate

## Overview
Build a set of microservices that support a simple hotel booking website that allows to perform CRUD operations on a booking by integrating with a Database and a 3rd Party Service for hotels

## Functional Requirements
 [x] As a client, I can create a new booking
 [x] As a client, I can read an existing booking
 [ ] As a client, I can change an existing booking
 [ ] As a client, I can delete an existing booking
 [ ] As a client, I can send a notification for bookings
> Note: This requirements are in priority order, use time boxing to deliver as many as you can in suggested time<

## Technical Requirements
- No client UI required
No client then.

- APIs run as embedded containers
Yes, using docker compose.

- Candidate can choose to persist booking against a DB or integrate with mocked 3rd party service (SOAP interface)
I chose a MongoDB

- Each API should take less than 500 milliseconds
500ms is an eternity, I would have liked to implement a histogram service for diagnosis of request 
processing times.  Spring Boot makes this quite easy to do.

- Build a mock that returns reference data for Hotels (pref SOAP)
SOAP Hotel Ref Data service done using Spring Boot.


## Review criteria
- REST URL naming and design approach to services
In this exercise the resource is the `Booking` class.  A read of an existing booking is idempotent
and so is available as a ReST GET with a path parameter.  
The store of a new booking is available as a ReST POST with a json payload.
Given more time, I would have maybe implemented the Rest service as HATEOS.

- usage of java features with comments & coding conventions/style
One thing I don't like about Spring Rest Controllers is that they are a bit inflexible given
that the rest methodshave to either return the body response class or a `ResponseEntity`  I prefer
how Jersey/DropWizard handle the response as I think it gives a more flexible approach to returning
a non-happy path response body.  I don't think there is a 100% tidy way to do this in Spring Boot.
I especially don't like using exception handlers to drive non exception flow (e.g. for 4xx responses) 

- usage of spring boot and other spring features
I used Spring Boot 2 with starters for Mongo DB, SOAP Web Services and ReST Web Services.  
I also used Spring to perform JSR303 validation.  Given more time, I would have made use 
of Spring actuator and features for metrics and enhanced healthcheck endpoints.

- testing approach for services
I provided Java api and in-container integration tests (via Palantir Docker Compose Rule).
Unfortunately, I could not get the Docker Compose Rule to work with junit5, so I used junit4.

- considerations like performance, security, logging, configurability
I used Spring Boot standards e.g. application.yaml, I used memoization for the Hotel Ref Data and added some Logging.
The logging was mainly used as a development aid, with more thought logging could be added as a 
cross-cutting concern

## Running the project
There are 3 docker containers that form this solution:
 * The Mongo database
 * The hotel ref data service
 * The ReST controller for bookings
```
 cd [project-root]
 ./gradlew clean build docker
 docker-compose up
```

## Using the ReST Services
I used Postman and RoboMongo to test the project after starting the services with `docker-compose`.

Here are some useful requests that can be run from command line to test the system

 1. Store a hotel booking:
 ```json
 curl -X POST \
   http://localhost:8080/booking \
   -H 'cache-control: no-cache' \
   -H 'content-type: application/json' \
   -d '{
 	"id" :  "6a808a2e-6abf-74ef-dca3-5235464afa20",
 	"numberOfNights" : 4,
 	"customer" : {"name" : "my name", "email" : "my-email@mail.com"},
 	"hotelId" : "1"
 }'
 ```
 
 2. Read an existing booking
 ```json
 curl -X GET \
   http://localhost:8080/booking/6a808a2e-6abf-74ef-dca3-5235464afa20 \
   -H 'cache-control: no-cache' \
   -H 'content-type: application/json' 
 ```
 
 Alternatively, You can use swagger with the json payloads above to call the services
 http://localhost:8080/swagger-ui.html
 