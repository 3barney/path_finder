package com.routing.test.RoutingTest.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.routing.test.RoutingTest.dto.request.Country;
import com.routing.test.RoutingTest.exception.NetworkServiceException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import org.springframework.test.web.client.RequestMatcher;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@SpringBootTest
class CountryServiceTest {

    private static String countriesMock;
    private static Country[] countryList;
    private static final Resource countriesBorderMockFIle = new ClassPathResource("countries_border_mock.json");

    @Autowired
    protected RestTemplate restTemplate;

    @Autowired
    private CountryService countryService;

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
    void when_getCountries_expect_listOfCountries() {
        var countryList = countryService.getCountries();
        mockRestServiceServer.verify();
        assertEquals(6, countryList.size());
    }

    @Test
    void when_getCountries_isEmpty_expectEmptyException() {
        mockRestServiceServer.reset();

        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(url))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON).body(""));

        var dataEmptyException = assertThrows(
                NetworkServiceException.class, () -> {
                    countryService.getCountries();
                });
        assertEquals("Empty List of countries", dataEmptyException.getMessage());
    }

    @Test
    void when_getCountries_isErrored_expectNetworkErrorException() {
        mockRestServiceServer.reset();

        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(url))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE)
                        .contentType(MediaType.APPLICATION_JSON).body(""));

        var dataEmptyException = assertThrows(
                NetworkServiceException.class, () -> {
                    countryService.getCountries();
                });
        assertEquals("An error occured querying for countries", dataEmptyException.getMessage());
    }
}