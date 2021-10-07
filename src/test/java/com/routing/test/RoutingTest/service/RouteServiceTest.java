package com.routing.test.RoutingTest.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.routing.test.RoutingTest.dto.request.Country;
import com.routing.test.RoutingTest.exception.CountryNotFoundException;
import com.routing.test.RoutingTest.exception.RouteNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@SpringBootTest
class RouteServiceTest {

    private static String countriesMock;
    private static Country[] countryList;
    private static final Resource countriesBorderMockFIle = new ClassPathResource("countries_border_mock.json");

    @Autowired
    protected RestTemplate restTemplate;

    @Autowired
    private RouteService routeService;

    @Value("${application.endpoint.countries}")
    protected String url;

    protected MockRestServiceServer mockRestServiceServer;

    @BeforeAll
    public static void init() throws Exception {
        countriesMock = Files.readString(countriesBorderMockFIle.getFile().toPath());
        countryList = new ObjectMapper().readValue(countriesMock, Country[].class);
    }

    @BeforeEach
    public void setupMockServer() throws Exception {
        mockRestServiceServer = MockRestServiceServer.bindTo(restTemplate).build();
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(url))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(countriesMock));
    }

    @Test
    void when_given_correctRoute_ensure_routeExists() {
        var route = routeService.findPath("KEN", "UGA");
        assertEquals(Arrays.asList("KEN", "UGA"), route.getRoute());
    }

    @Test
    void when_given_sameCountry_ensureSingleRouteExists() {
        var route = routeService.findPath("KEN", "KEN");
        assertEquals(Collections.singletonList("KEN"), route.getRoute());
    }

    @Test
    void when_givenWrongOriginCountry_EnsureCountryNotFoundException_isReturned() {
        var countryNotFoundException = assertThrows(CountryNotFoundException.class, () -> {
            routeService.findPath("XXX", "SVK");
        });
        assertEquals("Country XXX not found", countryNotFoundException.getMessage());
    }

    @Test
    void when_giveWrongDestinationCountry_EnsureCountryNotFoundException_isReturned() {
        var countryNotFoundException = assertThrows(CountryNotFoundException.class, () -> {
            routeService.findPath("KEN", "AAA");
        });
        assertEquals("Country AAA not found", countryNotFoundException.getMessage());
    }

    @Test
    void when_given_wrongRoute_throwException_withCustomMessage() {
        var routeNotFoundException = assertThrows(RouteNotFoundException.class, () -> {
            routeService.findPath("GRL", "KEN");
        });
        assertEquals("Country GRL is landlocked", routeNotFoundException.getMessage());
    }

}