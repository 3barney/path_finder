package com.routing.test.RoutingTest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.routing.test.RoutingTest.dto.request.Country;
import com.routing.test.RoutingTest.service.CountryService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Files;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RouteControllerTest {

    private static String countriesMock;
    private static Country[] countryList;
    private static final Resource countriesBorderMockFIle = new ClassPathResource("countries_border_mock.json");

    @Autowired
    protected RestTemplate restTemplate;

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


    @Autowired
    private MockMvc mockMvc;

    @Test
    void when_test_withSameCountryOfOriginAndDestination_returnSingleItem() throws Exception {
        this.mockMvc.perform(get("/route/KEN/KEN")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"route\":[\"KEN\"]}"));
    }

    @Test
    void when_test_withDifferentValidCountries_getListOfRoutes() throws Exception {
        this.mockMvc.perform(get("/route/KEN/UGA")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"route\":[\"KEN\",\"UGA\"]}"));
    }

    @Test
    void when_test_withLockedCountry_getCountryNotFoundError() throws Exception {
        this.mockMvc.perform(get("/route/XXX/KEN")).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(containsString("Country XXX not found")));
    }

    @Test
    void when_test_withInvalidRoute_GetCountryLocked() throws Exception {
        this.mockMvc.perform(get("/route/GRL/KEN")).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(containsString("Country GRL is landlocked")));
    }

    @Test
    void when_NetworkErrorOccurs_returnInternalServererror() throws Exception {

        mockRestServiceServer.reset();
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(url))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON));

        this.mockMvc.perform(get("/route/KEN/UGA")).andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(status().reason(containsString("An error occured querying for countries")));
    }
}