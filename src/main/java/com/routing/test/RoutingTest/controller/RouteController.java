package com.routing.test.RoutingTest.controller;

import com.routing.test.RoutingTest.dto.response.Route;
import com.routing.test.RoutingTest.exception.CountryNotFoundException;
import com.routing.test.RoutingTest.exception.NetworkServiceException;
import com.routing.test.RoutingTest.exception.RouteNotFoundException;
import com.routing.test.RoutingTest.service.RouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @Operation(summary = "Calculate any route between two countries using cca3 country name {KENYA -> KEN}", responses = {
            @ApiResponse(responseCode = "200", description = "Route found"),
            @ApiResponse(responseCode = "400", description = "Route or country not found", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "Network failure", content = @Content(schema = @Schema(hidden = true))) })
    @GetMapping(value = "/route/{origin}/{destination}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Route getAllRoutes(@PathVariable String origin, @PathVariable String destination) {

        try {
            return routeService.findPath(origin, destination);
        } catch (NetworkServiceException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex);
        } catch (RouteNotFoundException | CountryNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
    }
}
