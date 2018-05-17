package com.paulturner.hotels.configuration;

import com.paulturner.xsd.hotelrefdata.GetHotelRefDataRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.util.ClassUtils;
import org.springframework.ws.client.core.WebServiceTemplate;

@Configuration
public class ControllerConfiguration {

    @Bean
    public WebServiceTemplate webServiceTemplate() {
        try {
            Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
            marshaller.setPackagesToScan(ClassUtils.getPackageName(GetHotelRefDataRequest.class));
            marshaller.afterPropertiesSet();
            return new WebServiceTemplate(marshaller);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

}
