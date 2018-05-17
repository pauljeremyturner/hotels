package com.paulturner.hotels.controller;

import com.google.common.base.Strings;
import com.paulturner.hotels.model.Booking;
import com.paulturner.hotels.model.BookingRequest;
import com.paulturner.hotels.model.BookingStatus;
import com.paulturner.hotels.model.Hotel;
import com.paulturner.hotels.repository.HotelBookingRepository;
import com.paulturner.hotels.service.HotelRefDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@Slf4j
public class HotelBookingController {

    private final HotelBookingRepository repository;
    private final HotelRefDataService hotelRefDataService;

    @Autowired
    public HotelBookingController(HotelBookingRepository repository, HotelRefDataService hotelRefDataService) {
        this.repository = repository;
        this.hotelRefDataService = hotelRefDataService;
    }

    @RequestMapping(
            value = "booking/{bookingId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> readBooking(
            @Valid @PathVariable("bookingId") String bookingId
    ) {

        log.info("Read booking [id {}]", bookingId);

        final Optional<Booking> bookingOptional = repository.findById(bookingId);

        if (Strings.isNullOrEmpty(bookingId)) {
            return ResponseEntity.badRequest().build();
        }

        if (!bookingOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(bookingOptional.get());

    }

    @RequestMapping(
            value = "booking",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> storeBooking(
            @Valid @RequestBody BookingRequest request,
            Errors errors
    ) {

        log.info("Store booking [booking request {}]", request);

        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        log.info("Get Hotel data from ws/ cache [hotelId {}]", request.getHotelId());
        final Optional<Hotel> hotel = hotelRefDataService.getHotelById(request.getHotelId());
        if (!hotel.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        final Booking booking = new Booking(
                request.getId(),
                request.getNumberOfNights(),
                BookingStatus.CONFIRMED,
                request.getCustomer(),
                hotel.get()
        );

        log.info("Save booking [booking {}]", booking);
        final Booking saved = repository.save(booking);

        return ResponseEntity.ok(saved);
    }

}
