package com.paulturner.hotels.repository;

import com.paulturner.hotels.model.Booking;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface HotelBookingRepository extends MongoRepository<Booking, String> {


}
