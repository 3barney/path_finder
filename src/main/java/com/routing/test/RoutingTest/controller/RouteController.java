package com.routing.test.RoutingTest.controller;

import com.routing.test.RoutingTest.dto.request.Country;
import com.routing.test.RoutingTest.exception.NetworkServiceException;
import com.routing.test.RoutingTest.service.CountryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@RestController
public class RouteController {

    private final CountryService countryService;

    public RouteController(CountryService countryService) {
        this.countryService = countryService;
    }

    @Operation(summary = "Calculate any possible land route from one country to another", responses = {
            @ApiResponse(responseCode = "200", description = "Route found"),
            @ApiResponse(responseCode = "400", description = "Route impossible or country not found", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "Data service failure", content = @Content(schema = @Schema(hidden = true))) })
    @GetMapping(value = "/routing/{origin}/{destination}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Set<Country> getAllCountries() {

        try {
            return countryService.getCountries();
        } catch (NetworkServiceException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex);
        }
    }
}
