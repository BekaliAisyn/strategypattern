package com.aitu.delivery.model;

import java.util.Set;

public record DeliveryRequest(String requestId, DeliveryPriority priority, double maxBudget,
                              int maxDeliveryMinutes, boolean ecoPreferred,
                              double maxCarbonEmissionKg, Set<String> prohibitedRouteIds) {
    public DeliveryRequest {
        if (requestId == null || requestId.isBlank()) throw new IllegalArgumentException("Request id must not be blank");
        if (priority == null) throw new IllegalArgumentException("Priority is required");
        if (!Double.isFinite(maxBudget) || maxBudget < 0) throw new IllegalArgumentException("Max budget must be finite and non-negative");
        if (maxDeliveryMinutes < 0) throw new IllegalArgumentException("Maximum delivery minutes must be non-negative");
        if (!Double.isFinite(maxCarbonEmissionKg) || maxCarbonEmissionKg < 0) throw new IllegalArgumentException("Maximum emissions must be finite and non-negative");
        prohibitedRouteIds = prohibitedRouteIds == null ? Set.of() : Set.copyOf(prohibitedRouteIds);
    }
    public static DeliveryRequest standard(String id, DeliveryPriority priority, double budget, int deadline) {
        return new DeliveryRequest(id, priority, budget, deadline, priority == DeliveryPriority.ECO,
                Double.MAX_VALUE, Set.of());
    }
}
