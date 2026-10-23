package com.aitu.delivery.strategy;
import com.aitu.delivery.model.*; import java.util.*;
public final class FastestRouteStrategy extends AbstractRouteStrategy {
 public RouteDecision selectRoute(List<Route> routes, DeliveryRequest request) {
  Route r=eligibleRoutes(routes,request).stream().min(Comparator.comparingInt(Route::effectiveMinutes).thenComparingDouble(Route::cost)).orElseThrow();
  return new RouteDecision(r,getStrategyName(),r.effectiveMinutes(),List.of("Selected the lowest effective delivery time.","Base time: "+r.estimatedMinutes()+" min; traffic penalty: +"+r.trafficPenaltyMinutes()+" min; final time: "+r.effectiveMinutes()+" min.",commonExplanation(r)));
 }
 public String getStrategyName(){return "Fastest";}
}
