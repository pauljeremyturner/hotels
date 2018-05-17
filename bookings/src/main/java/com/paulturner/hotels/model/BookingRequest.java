package com.paulturner.hotels.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class BookingRequest {

    private String id;

    @Min(1L)
    private Long numberOfNights;

    @NotNull
    private Customer customer;

    @NotNull
    private String hotelId;

}
