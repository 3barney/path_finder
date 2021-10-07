package com.routing.test.RoutingTest.service;

import com.routing.test.RoutingTest.dto.request.Country;
import com.routing.test.RoutingTest.dto.response.Route;
import com.routing.test.RoutingTest.exception.CountryNotFoundException;
import com.routing.test.RoutingTest.exception.RouteNotFoundException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.BFSShortestPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedPseudograph;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Set;

@Service
public class RouteServiceImpl implements RouteService {

    private final CountryService countryService;

    public RouteServiceImpl(CountryService countryService) {
        this.countryService = countryService;
    }
    
    @Override
    public Route findPath(String origin, String destination) {
        
        var countries = countryService.getCountries();
        var countryOfOrigin = findCountryByName(origin, countries);
        var countryOfDestination = findCountryByName(destination, countries);

        if (!countryOfOrigin.equals(countryOfDestination)) {
            if (countryOfOrigin.getBorders().isEmpty()) {
                throw new RouteNotFoundException(String.format("Country %s is landlocked", countryOfOrigin.getName()));
            }
            if (countryOfDestination.getBorders().isEmpty()) {
                throw new RouteNotFoundException(String.format("Country %s is landlocked", countryOfDestination.getName()));
            }
        }


        var graph = buildGraph(countries);

        GraphPath<String, DefaultEdge> path = DijkstraShortestPath.findPathBetween(graph, origin, destination);
        if (path == null) {
            throw new RouteNotFoundException(String.format("Route from %s to %s not found", origin, destination));
        }

        return new Route(path.getVertexList());
        
    }
    
    private Country findCountryByName(String countryCode, Set<Country> countries) {
        return countries
                .stream()
                .filter(country -> country.getName().equalsIgnoreCase(countryCode))
                .findFirst()
                .orElseThrow(() -> new CountryNotFoundException(countryCode));
    }

    private DirectedPseudograph<String, DefaultEdge> buildGraph(Set<Country> countries) {

        DirectedPseudograph<String, DefaultEdge> graph = new DirectedPseudograph<>(DefaultEdge.class);

        countries.forEach(c -> {
            graph.addVertex(c.getName());
            c.getBorders().forEach(b -> {
                graph.addVertex(b);
                graph.addEdge(c.getName(), b);
            });
        });

        return graph;
    }
}
