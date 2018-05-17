package com.paulturner.hotels.hotelrefdata;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class HotelRefDataRepositoryTest {

    private HotelRefDataRepository hotelRefDataRepository;

    @Before
    public void before() {
        hotelRefDataRepository = new HotelRefDataRepository();
        hotelRefDataRepository.postConstruct();
    }

    @Test
    public void shouldLoadHotelRefData() throws Exception {
        assertThat(hotelRefDataRepository.findHotel("1").getId(), is("1"));
        assertThat(hotelRefDataRepository.findHotel("1").getName(), is("Holiday Inn Express Glasgow Airport"));
    }

}