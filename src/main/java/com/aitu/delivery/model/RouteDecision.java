package com.aitu.delivery.model;

import java.util.List;

public record RouteDecision(Route route, String strategyName, double score, List<String> explanation) {
    public RouteDecision {
        if (route == null) throw new IllegalArgumentException("Selected route is required");
        if (strategyName == null || strategyName.isBlank()) throw new IllegalArgumentException("Strategy name is required");
        if (!Double.isFinite(score)) throw new IllegalArgumentException("Score must be finite");
        explanation = explanation == null ? List.of() : List.copyOf(explanation);
    }
}
