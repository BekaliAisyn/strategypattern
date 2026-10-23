package com.aitu.delivery.planner;
import com.aitu.delivery.model.*; import com.aitu.delivery.strategy.RouteStrategy; import java.util.List; import java.util.Objects;
/** Context: delegates route selection to the currently configured Strategy. */
public final class RoutePlanner {
 private RouteStrategy strategy;
 public RoutePlanner(RouteStrategy strategy){setStrategy(strategy);}
 public void setStrategy(RouteStrategy strategy){this.strategy=Objects.requireNonNull(strategy,"Strategy must not be null");}
 public RouteStrategy getStrategy(){return strategy;}
 public RouteDecision plan(List<Route> routes,DeliveryRequest request){return strategy.selectRoute(routes,request);}
}
