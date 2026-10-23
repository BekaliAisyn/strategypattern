package com.aitu.delivery.strategy;

import com.aitu.delivery.exception.NoSuitableRouteException;
import com.aitu.delivery.model.DeliveryRequest;
import com.aitu.delivery.model.Route;
import java.util.List;

abstract class AbstractRouteStrategy implements RouteStrategy {
    protected List<Route> eligibleRoutes(List<Route> routes, DeliveryRequest request) {
        if (routes == null || routes.isEmpty()) throw new NoSuitableRouteException("No candidate routes were supplied.");
        if (request == null) throw new IllegalArgumentException("Delivery request is required");
        List<Route> eligible = routes.stream().filter(r -> r != null)
            .filter(Route::available)
            .filter(r -> !request.prohibitedRouteIds().contains(r.id()))
            .filter(r -> r.cost() <= request.maxBudget())
            .filter(r -> r.effectiveMinutes() <= request.maxDeliveryMinutes())
            .filter(r -> r.carbonEmissionKg() <= request.maxCarbonEmissionKg()).toList();
        if (eligible.isEmpty()) throw new NoSuitableRouteException("No route satisfies availability, prohibited-route, budget, deadline, and emission constraints for request " + request.requestId());
        return eligible;
    }
    protected String commonExplanation(Route route) {
        return "Hard constraints passed: available, budget, deadline, emission limit, and route restrictions.";
    }
}
