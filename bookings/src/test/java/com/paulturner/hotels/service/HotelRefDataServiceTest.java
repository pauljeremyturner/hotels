package com.paulturner.hotels.service;

import com.paulturner.xsd.hotelrefdata.GetHotelRefDataRequest;
import com.paulturner.xsd.hotelrefdata.GetHotelRefDataResponse;
import com.paulturner.xsd.hotelrefdata.HotelRefData;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;
import org.springframework.ws.client.core.WebServiceTemplate;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

public class HotelRefDataServiceTest {

    private HotelRefDataServiceFixture fixture;

    @Before
    public void before() throws Exception {
        fixture = new HotelRefDataServiceFixture();
    }

    @Test
    public void existingHotel() throws Exception {
        String id = UUID.randomUUID().toString();

        fixture
                .givenWebServiceProvidesHotelForId(id)
                .expectHotelWithId(id);
    }

    @Test
    public void nonExistentHotel() throws Exception {
        String id = UUID.randomUUID().toString();

        fixture
                .givenWebServiceProvidesNullForId(id)
                .expectEmptyResponse(id);
    }

    private static class HotelRefDataServiceFixture {

        private WebServiceTemplate mockWebServiceTemplate;
        private HotelRefDataService service;

        public HotelRefDataServiceFixture() {
            mockWebServiceTemplate = Mockito.mock(WebServiceTemplate.class);
            final Environment mockEnvironment = Mockito.mock(Environment.class);

            Mockito
                    .when(mockEnvironment.getProperty(anyString()))
                    .thenReturn("http://host:80/ws");

            service = new HotelRefDataService(
                    mockWebServiceTemplate, mockEnvironment
            );
        }

        public HotelRefDataServiceFixture givenWebServiceProvidesHotelForId(final String id) {
            final HotelRefData hotel = new HotelRefData();
            hotel.setId(id);
            hotel.setName("Hotel Name");
            final GetHotelRefDataResponse response = new GetHotelRefDataResponse();
            response.setHotel(hotel);

            Mockito
                    .when(mockWebServiceTemplate.marshalSendAndReceive(anyString(), any(GetHotelRefDataRequest.class)))
                    .thenReturn(response);

            return this;

        }

        public HotelRefDataServiceFixture givenWebServiceProvidesNullForId(final String id) {
            final GetHotelRefDataResponse response = new GetHotelRefDataResponse();

              Mockito
                    .when(mockWebServiceTemplate.marshalSendAndReceive(anyString(), any(GetHotelRefDataRequest.class)))
                    .thenReturn(response);

            return this;

        }


        public HotelRefDataServiceFixture expectHotelWithId(final String id) {

            assertThat(service.getHotelById(id).get().getId(), is(id));

            return this;
        }

        public HotelRefDataServiceFixture expectEmptyResponse(final String id) {

            assertFalse(service.getHotelById(id).isPresent());

            return this;
        }
    }

}