package com.routing.test.RoutingTest.service;

import com.routing.test.RoutingTest.dto.request.Country;
import com.routing.test.RoutingTest.dto.response.Route;

public interface RouteService {

    /**
     * Find shortest path between two countries
     *
     * @param origin country of origin            {@link Country#getName()}
     * @param destination country of destination  {@link Country#getName()}
     *
     * @return list of {@link Route}
     */
    Route findPath(String origin, String destination);
}
