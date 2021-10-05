package com.routing.test.RoutingTest.exception;

public class RouteNotFoundException extends RuntimeException {
    public RouteNotFoundException(String reason) {
        super(reason);
    }
}
