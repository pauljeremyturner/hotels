package com.paulturner.hotels.controller;

import com.palantir.docker.compose.DockerComposeRule;
import com.palantir.docker.compose.connection.DockerPort;
import com.palantir.docker.compose.connection.waiting.HealthChecks;
import com.paulturner.hotels.model.*;
import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static org.junit.Assert.assertThat;

public class BookingControllerIT {

    private final RestTemplate restTemplate = new RestTemplate();

    @ClassRule
    public static DockerComposeRule docker = DockerComposeRule.builder()
            .file("src/integration-test/resources/docker-compose-it.yml")
            .waitingForService("mongodb", HealthChecks.toHaveAllPortsOpen())
            .waitingForService("hotelrefdata", HealthChecks.toHaveAllPortsOpen())
            .waitingForService("bookings", HealthChecks.toHaveAllPortsOpen())
            .build();

    @Test
    public void shouldStoreAndReadBooking() throws Exception {

        DockerPort bookings = docker.containers()
                .container("bookings")
                .port(8080);

        String url = bookings.inFormat("http://$HOST:$EXTERNAL_PORT/booking/");

        final Customer customer = new Customer("Alice", "alice@mail.com");
        final BookingRequest request = new BookingRequest(
                UUID.randomUUID().toString(),
                3L,
                customer,
                "3"
        );

        final Booking expectedBooking = new Booking(
                request.getId(),
                request.getNumberOfNights(),
                BookingStatus.CONFIRMED,
                request.getCustomer(),
                new Hotel("3", "Ibis Glasgow City Centre")
        );

        HttpEntity<BookingRequest> bookingRequestHttpEntity = new HttpEntity<>(request);

        Awaitility.await().atMost(Duration.TEN_SECONDS).until( () ->
                {
                    try {
                        restTemplate.exchange(url, HttpMethod.POST, bookingRequestHttpEntity, Booking.class);
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                }
        );

        Awaitility.await().atMost(Duration.TEN_SECONDS).until( () ->
                {
                    try {
                        final ResponseEntity<Booking> responseEntity = restTemplate.exchange(url + request.getId(), HttpMethod.GET, null, Booking.class);
                        final Booking actualBooking = responseEntity.getBody();
                        return actualBooking.getId().equals(expectedBooking.getId());
                    } catch (Exception e) {
                        return false;
                    }
                }
        );
    }


}