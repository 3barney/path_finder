package com.routing.test.RoutingTest.service;

import com.routing.test.RoutingTest.dto.request.Country;

import java.util.Set;

public interface CountryService {

    /**
     * Fetch api to get a list of countries
     *
     * @return list of {@link Country}
     */
    Set<Country> getCountries();
}