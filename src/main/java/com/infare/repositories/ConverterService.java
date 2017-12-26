package com.infare.repositories;

import com.infare.domain.ConvertedValue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ConverterService {

    @Value("${converter.service.url}")
    String SERVICE_URL;
    
    RestTemplate restApi;
    
    public ConverterService(RestTemplate restApi) {
        this.restApi = restApi;
    }

    public Double convert(Double value) {
        final ConvertedValue response = restApi.getForObject(SERVICE_URL, ConvertedValue.class, value);
        return response.getPrice();
    }

}
