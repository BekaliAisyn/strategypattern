package com.aitu.delivery.strategy;

import com.aitu.delivery.model.DeliveryRequest;
import com.aitu.delivery.model.Route;
import com.aitu.delivery.model.RouteDecision;
import java.util.List;

public interface RouteStrategy {
    RouteDecision selectRoute(List<Route> routes, DeliveryRequest request);
    String getStrategyName();
}
