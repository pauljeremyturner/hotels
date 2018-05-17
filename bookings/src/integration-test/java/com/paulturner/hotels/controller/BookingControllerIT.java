package com.paulturner.hotels.controller;

import com.palantir.docker.compose.DockerComposeRule;
import com.palantir.docker.compose.connection.DockerPort;
import com.palantir.docker.compose.connection.waiting.HealthChecks;
import com.paulturner.hotels.model.*;
import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

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
    public void shouldStoreAndReadBooking() {

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
        restTemplate.exchange(url, HttpMethod.POST, bookingRequestHttpEntity, Booking.class);

        Awaitility
                .await().
                atMost(Duration.TEN_SECONDS)
                .until(() -> assertResponse(url + request.getId(), expectedBooking));
    }

    private boolean assertResponse(String url, Booking expectedBooking) {
        final ResponseEntity<Booking> responseEntity = restTemplate.exchange(url, HttpMethod.GET, null, Booking.class);

        final Booking actualBooking = responseEntity.getBody();

        boolean bookingIdsOk = actualBooking.getId().equals(expectedBooking.getId());
        return bookingIdsOk;
    }

}