package com.aitu.delivery.strategy;
import com.aitu.delivery.model.*; import java.util.*;
/** Fifth strategy added independently: prefer the highest safety score among eligible routes. */
public final class SafestRouteStrategy extends AbstractRouteStrategy {
 public RouteDecision selectRoute(List<Route> routes,DeliveryRequest request){Route r=eligibleRoutes(routes,request).stream().max(Comparator.comparingDouble(Route::safetyScore).thenComparing(Comparator.comparingInt(Route::effectiveMinutes).reversed())).orElseThrow();return new RouteDecision(r,getStrategyName(),10-r.safetyScore(),List.of("Selected the route with the highest safety score.",String.format(Locale.US,"Safety score: %.1f/10.",r.safetyScore()),commonExplanation(r)));}
 public String getStrategyName(){return "Safest";}
}
