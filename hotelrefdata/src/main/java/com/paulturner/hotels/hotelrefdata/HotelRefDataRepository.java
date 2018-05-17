package com.paulturner.hotels.hotelrefdata;

import com.paulturner.xsd.hotelrefdata.HotelRefData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

@Component
@Slf4j
public class HotelRefDataRepository {
    private Map<String, HotelRefData> hotels;



    @PostConstruct
    public void postConstruct() {
        try (
                InputStream is = HotelRefDataRepository.class.getResourceAsStream("/hotelrefdata/hotels.csv");
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
        ) {
            Map<String, HotelRefData> tmpHotels = new HashMap<>();
           br.lines().forEach(
                    l -> {
                       String[] parts = l.split(",");
                        HotelRefData hotelRefData = new HotelRefData();
                        hotelRefData.setId(parts[0]);
                        hotelRefData.setName(parts[1]);
                        tmpHotels.put(hotelRefData.getId(), hotelRefData);
                    }
            );
            this.hotels = Collections.unmodifiableMap(tmpHotels);

            log.info("hotelrefdata [hotels {}]", hotels);

        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public HotelRefData findHotel(final String id) {

        requireNonNull(id);
        final HotelRefData hotel = hotels.get(id);
        requireNonNull(id);

        return hotel;
    }
}
