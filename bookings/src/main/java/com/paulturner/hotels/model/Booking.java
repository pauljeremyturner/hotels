package com.paulturner.hotels.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Booking {

    @Id
    private String id;

    private Long numberOfNights;

    private BookingStatus bookingStatus;

    private Customer customer;

    private Hotel hotel;

}
