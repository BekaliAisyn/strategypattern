package com.aitu.delivery.strategy;
import com.aitu.delivery.model.*; import java.util.*;
public final class CheapestRouteStrategy extends AbstractRouteStrategy {
 public RouteDecision selectRoute(List<Route> routes, DeliveryRequest request) {
  Route r=eligibleRoutes(routes,request).stream().min(Comparator.comparingDouble(Route::cost).thenComparingInt(Route::effectiveMinutes)).orElseThrow();
  return new RouteDecision(r,getStrategyName(),r.cost(),List.of("Selected the lowest total delivery cost.","Cost formula: base route cost + any fees already included in Route.cost.","Total cost: "+String.format(Locale.US,"%.2f",r.cost()),commonExplanation(r)));
 }
 public String getStrategyName(){return "Cheapest";}
}
