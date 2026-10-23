package com.aitu.delivery;

import com.aitu.delivery.model.*;
import com.aitu.delivery.planner.RoutePlanner;
import com.aitu.delivery.selector.StrategySelector;
import com.aitu.delivery.service.StrategyComparisonService;
import com.aitu.delivery.strategy.*;
import java.util.*;

public final class DemoApplication {
    private DemoApplication() {}

    public static void main(String[] args) {
        List<Route> routes = sampleRoutes();
        DeliveryRequest request = new DeliveryRequest("REQ-105", DeliveryPriority.STANDARD, 7000, 60, true, 8.0, Set.of("R8"));
        RoutePlanner planner = new RoutePlanner(BalancedRouteStrategy.defaults());
        System.out.println("SMART DELIVERY & ROUTE OPTIMIZATION ENGINE");
        System.out.println("Request: " + request.requestId() + " | priority=" + request.priority() + " | budget=" + request.maxBudget() + " | deadline=" + request.maxDeliveryMinutes() + " min\n");

        StrategySelector selector = new StrategySelector();
        planner.setStrategy(selector.select(request));
        print(planner.plan(routes, request));

        System.out.println("\nRUNTIME STRATEGY SWITCHING (same RoutePlanner instance)");
        for (RouteStrategy strategy : List.of(new FastestRouteStrategy(), new CheapestRouteStrategy(), new EcoFriendlyRouteStrategy(), BalancedRouteStrategy.defaults())) {
            planner.setStrategy(strategy);
            print(planner.plan(routes, request));
        }

        System.out.println("\nSTRATEGY COMPARISON");
        Map<String, RouteDecision> results = new StrategyComparisonService().compare(
            List.of(new FastestRouteStrategy(), new CheapestRouteStrategy(), new EcoFriendlyRouteStrategy(), BalancedRouteStrategy.defaults(), new SafestRouteStrategy()), routes, request);
        results.forEach((name, decision) -> System.out.printf(Locale.US,
            "%-13s -> %-3s | time=%2d min | cost=%7.2f | CO2=%4.1f kg | score=%.4f%n",
            name, decision.route().id(), decision.route().effectiveMinutes(), decision.route().cost(), decision.route().carbonEmissionKg(), decision.score()));

        System.out.println("\nAUTOMATIC SELECTION EXAMPLES");
        for (DeliveryPriority priority : DeliveryPriority.values()) {
            DeliveryRequest sample = new DeliveryRequest("REQ-" + priority, priority, 7000, 60, priority == DeliveryPriority.ECO, 8.0, Set.of());
            System.out.println(priority + " -> " + selector.select(sample).getStrategyName());
        }
    }

    private static void print(RouteDecision decision) {
        System.out.printf(Locale.US, "Strategy: %s | selected: %s | time: %d min | cost: %.2f | CO2: %.1f kg | score: %.4f%n",
            decision.strategyName(), decision.route().id(), decision.route().effectiveMinutes(), decision.route().cost(), decision.route().carbonEmissionKg(), decision.score());
        decision.explanation().forEach(line -> System.out.println("  - " + line));
    }

    public static List<Route> sampleRoutes() {
        Location origin = new Location("AITU Logistics Hub", 51.0902, 71.4181);
        Location destination = new Location("Delivery Destination", 51.1280, 71.4300);
        return List.of(
            new Route("R1", origin, destination, 12, 24, 6100, 5.4, true, 4, 8.2),
            new Route("R2", origin, destination, 16, 28, 5200, 4.7, true, 2, 8.8),
            new Route("R3", origin, destination, 10, 34, 4700, 3.2, true, 3, 9.1),
            new Route("R4", origin, destination, 18, 42, 3900, 2.1, true, 5, 9.5),
            new Route("R5", origin, destination, 22, 48, 2800, 6.8, true, 1, 7.4),
            new Route("R6", origin, destination, 14, 31, 5500, 2.8, true, 0, 9.7),
            new Route("R7", origin, destination, 20, 38, 4300, 4.0, false, 0, 9.9),
            new Route("R8", origin, destination, 8, 20, 6600, 1.4, true, 1, 6.9));
    }
}
