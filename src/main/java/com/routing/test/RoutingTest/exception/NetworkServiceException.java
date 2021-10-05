package com.routing.test.RoutingTest.exception;

public class NetworkServiceException extends RuntimeException {

    public NetworkServiceException(String reason) {
        super(reason);
    }

    public NetworkServiceException(String reason, Throwable exception) {
        super(reason, exception);
    }
}
