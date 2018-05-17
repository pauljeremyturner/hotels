package com.paulturner.hotels.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.paulturner.hotels.model.Hotel;
import com.paulturner.xsd.hotelrefdata.GetHotelRefDataRequest;
import com.paulturner.xsd.hotelrefdata.GetHotelRefDataResponse;
import com.paulturner.xsd.hotelrefdata.HotelRefData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
public class HotelRefDataService {

    private final WebServiceTemplate webServiceTemplate;
    private final LoadingCache<String, Hotel> hotelCache;
    private final String wsUrl;

    @Autowired
    public HotelRefDataService(WebServiceTemplate webServiceTemplate, Environment environment) {
        this.webServiceTemplate = webServiceTemplate;
        this.wsUrl = environment.getProperty("hotelrefdata.url");

        log.info("Configured for SOAP HotelRefData [url {}]", this.wsUrl);

        hotelCache = CacheBuilder.newBuilder().build(
                new CacheLoader<String, Hotel>() {
                    @Override
                    public Hotel load(String id) throws Exception {
                        return getHotel(id);
                    }
                }
        );
    }

    public Optional<Hotel> getHotelById(String id) {
        try {
            return Optional.of(hotelCache.getUnchecked(id));
        } catch (CacheLoader.InvalidCacheLoadException ice) {
            log.warn("Request for missing hotel [hotel id {}]", id);
            return Optional.empty();
        }
    }

    private Hotel getHotel(String id) {
        log.info("Cache miss for hotel [hotel id = {}]", id);
        GetHotelRefDataRequest hotelRequest = new GetHotelRefDataRequest();
        hotelRequest.setId(id);
        HotelRefData hotelRefData = ((GetHotelRefDataResponse) webServiceTemplate.marshalSendAndReceive(wsUrl, hotelRequest)).getHotel();
        Hotel hotel = null;
        if (Objects.nonNull(hotelRefData)) {
            hotel = new Hotel(hotelRefData.getId(), hotelRefData.getName());
        }
        log.info("Got hotel via SOAP [hotel {}]", hotel);

        return hotel;
    }

}
