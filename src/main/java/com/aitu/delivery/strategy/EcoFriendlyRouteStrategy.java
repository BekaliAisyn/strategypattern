package com.aitu.delivery.strategy;
import com.aitu.delivery.model.*; import java.util.*;
public final class EcoFriendlyRouteStrategy extends AbstractRouteStrategy {
 private final double emissionWeight, distanceWeight;
 public EcoFriendlyRouteStrategy(){this(0.8,0.2);} public EcoFriendlyRouteStrategy(double emissionWeight,double distanceWeight){if(!Double.isFinite(emissionWeight)||!Double.isFinite(distanceWeight)||emissionWeight<0||distanceWeight<0||Math.abs(emissionWeight+distanceWeight-1.0)>1e-9)throw new IllegalArgumentException("Eco weights must be non-negative and sum to 1.0");this.emissionWeight=emissionWeight;this.distanceWeight=distanceWeight;}
 public RouteDecision selectRoute(List<Route> routes, DeliveryRequest request) {
  List<Route> eligible=eligibleRoutes(routes,request); double maxE=eligible.stream().mapToDouble(Route::carbonEmissionKg).max().orElse(0), maxD=eligible.stream().mapToDouble(Route::distanceKm).max().orElse(0);
  Route r=eligible.stream().min(Comparator.comparingDouble(x->score(x,maxE,maxD))).orElseThrow(); double score=score(r,maxE,maxD);
  return new RouteDecision(r,getStrategyName(),score,List.of(String.format(Locale.US,"Eco score = normalized emissions × %.2f + normalized distance × %.2f.",emissionWeight,distanceWeight),String.format(Locale.US,"Emissions: %.2f kg; distance: %.2f km; score: %.4f.",r.carbonEmissionKg(),r.distanceKm(),score),commonExplanation(r)));
 }
 private double score(Route r,double maxE,double maxD){double e=maxE==0?0:r.carbonEmissionKg()/maxE;double d=maxD==0?0:r.distanceKm()/maxD;return e*emissionWeight+d*distanceWeight;}
 public String getStrategyName(){return "Eco-Friendly";}
}
