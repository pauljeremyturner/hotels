package com.paulturner.hotels.hotelrefdata;

import com.paulturner.xsd.hotelrefdata.GetHotelRefDataRequest;
import com.paulturner.xsd.hotelrefdata.GetHotelRefDataResponse;
import com.paulturner.xsd.hotelrefdata.HotelRefData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
@Slf4j
public class HotelRefDataEndpoint {
    private static final String NAMESPACE_URI = "http://paulturner.com/xsd/hotelrefdata.xsd";

    private final HotelRefDataRepository hotelRefDataRepository;

    @Autowired
    public HotelRefDataEndpoint(final HotelRefDataRepository hotelRefDataRepository) {
        this.hotelRefDataRepository = hotelRefDataRepository;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getHotelRefDataRequest")
    @ResponsePayload
    public GetHotelRefDataResponse findById(@RequestPayload final GetHotelRefDataRequest request) {

        log.info("get hotel by id [hotel id = {}]", request.getId());
        final GetHotelRefDataResponse response = new GetHotelRefDataResponse();

        final HotelRefData hotel = hotelRefDataRepository.findHotel(request.getId());
        response.setHotel(hotel);

        log.info("found hotel by id [hotel {}]", response.getHotel());

        return response;
    }
}
