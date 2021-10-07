package com.routing.test.RoutingTest.service;

import com.routing.test.RoutingTest.dto.request.Country;
import com.routing.test.RoutingTest.exception.NetworkServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CountryServiceImpl implements CountryService {

    private final String countriesEndpoint;
    private final RestTemplate restTemplate;

    public CountryServiceImpl(@Value("${application.endpoint.countries}") String endpoint, RestTemplate restTemplate) {
        this.countriesEndpoint = endpoint;
        this.restTemplate = restTemplate;
    }

    @Override
    public Set<Country> getCountries() {

        try {
            Country[] countriesData = restTemplate.getForObject(countriesEndpoint, Country[].class);

            if (Objects.isNull(countriesData)) {
                throw new NetworkServiceException("Empty List of countries");
            }

            return Arrays.stream(countriesData).collect(Collectors.toSet());
        } catch (HttpStatusCodeException ex) {
            throw new NetworkServiceException("An error occured querying for countries", ex);
        }
    }
}
