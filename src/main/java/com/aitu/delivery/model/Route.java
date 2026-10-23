package com.aitu.delivery.model;

public record Route(String id, Location origin, Location destination, double distanceKm,
                    int estimatedMinutes, double cost, double carbonEmissionKg,
                    boolean available, int trafficPenaltyMinutes, double safetyScore) {
    public Route {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Route id must not be blank");
        if (origin == null || destination == null) throw new IllegalArgumentException("Origin and destination are required");
        if (!Double.isFinite(distanceKm) || distanceKm < 0) throw new IllegalArgumentException("Distance must be finite and non-negative");
        if (estimatedMinutes < 0) throw new IllegalArgumentException("Estimated minutes must be non-negative");
        if (!Double.isFinite(cost) || cost < 0) throw new IllegalArgumentException("Cost must be finite and non-negative");
        if (!Double.isFinite(carbonEmissionKg) || carbonEmissionKg < 0) throw new IllegalArgumentException("Emissions must be finite and non-negative");
        if (trafficPenaltyMinutes < 0) throw new IllegalArgumentException("Traffic penalty must be non-negative");
        if (!Double.isFinite(safetyScore) || safetyScore < 0 || safetyScore > 10) throw new IllegalArgumentException("Safety score must be between 0 and 10");
    }
    public int effectiveMinutes() { return Math.addExact(estimatedMinutes, trafficPenaltyMinutes); }
}
