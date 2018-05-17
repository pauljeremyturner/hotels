package com.paulturner.hotels.controller;

import com.paulturner.hotels.model.*;
import com.paulturner.hotels.repository.HotelBookingRepository;
import com.paulturner.hotels.service.HotelRefDataService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;

import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;

public class BookingControllerTest {

    private HotelBookingControllerFixture fixture;

    @Before
    public void before() {
        this.fixture = new HotelBookingControllerFixture();
    }

    @Test
    public void shouldSaveHotelBooking() throws Exception {

        final Customer customer = new Customer("Alice", "alice@mail.com");
        final BookingRequest request = new BookingRequest(
                UUID.randomUUID().toString(),
                3L,
                customer,
                "hotel1"
        );

        final Booking expectedBooking = new Booking(
                request.getId(),
                request.getNumberOfNights(),
                BookingStatus.CONFIRMED,
                request.getCustomer(),
                new Hotel("hotel1", "Hotel Name")
        );

        fixture
                .givenHotelExists(expectedBooking.getHotel().getId(), expectedBooking.getHotel().getName())
                .whenSaveEndpointCalled(request)
                .expectBookingPersisted(expectedBooking);
    }

    @Test
    public void shouldReturnHttp400WhenInvalidRequest() throws Exception {
        final Customer customer = new Customer("Bob", "bob@mail.com");
        final BookingRequest request = new BookingRequest(
                UUID.randomUUID().toString(),
                0L,
                customer,
                "hotel1"
        );

        fixture
                .givenInvalidRequest()
                .whenSaveEndpointCalled(request)
                .expectInvalidStatus();
    }


    private static class HotelBookingControllerFixture {

        private HotelRefDataService hotelRefDataService = Mockito.mock(HotelRefDataService.class);
        private HotelBookingRepository mockRepository = Mockito.mock(HotelBookingRepository.class);
        private HotelBookingController controller = new HotelBookingController(mockRepository, hotelRefDataService);
        private Errors mockErrors = Mockito.mock(Errors.class);

        private ResponseEntity<?> responseEntity;

        public HotelBookingControllerFixture expectInvalidStatus() {
            assertThat(responseEntity.getStatusCode(), is(HttpStatus.BAD_REQUEST));

            return this;

        }


        public HotelBookingControllerFixture givenInvalidRequest() {
            Mockito.when(mockErrors.hasErrors()).thenReturn(Boolean.TRUE);

            return this;
        }

        public HotelBookingControllerFixture givenHotelExists(String hotelId, String hotelName) {

            Hotel hotel = new Hotel(hotelId, hotelName);

            Mockito
                    .when(hotelRefDataService.getHotelById(Mockito.eq(hotelId)))
                    .thenReturn(Optional.of(hotel));

            return this;
        }

        public HotelBookingControllerFixture whenSaveEndpointCalled(BookingRequest request) {
            responseEntity = controller.storeBooking(request, mockErrors);

            return this;
        }

        public HotelBookingControllerFixture expectBookingPersisted(Booking expectedBooking) {

            Mockito.when(mockErrors.hasErrors()).thenReturn(Boolean.FALSE);
            ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);

            Mockito.verify(mockRepository, times(1)).save(bookingCaptor.capture());

            Booking actualBooking = bookingCaptor.getValue();

            assertThat(actualBooking.getNumberOfNights(), is(expectedBooking.getNumberOfNights()));
            assertThat(actualBooking.getId(), is(expectedBooking.getId()));
            assertThat(actualBooking.getHotel(), is(expectedBooking.getHotel()));
            assertThat(actualBooking.getCustomer(), is(expectedBooking.getCustomer()));

            assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));

            return this;
        }


    }

}