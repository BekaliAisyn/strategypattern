package com.aitu.delivery.strategy;
import com.aitu.delivery.model.*; import java.util.*;
public final class BalancedRouteStrategy extends AbstractRouteStrategy {
 private final double timeWeight,costWeight,distanceWeight,emissionWeight;
 public BalancedRouteStrategy(double timeWeight,double costWeight,double distanceWeight,double emissionWeight){double[] w={timeWeight,costWeight,distanceWeight,emissionWeight};double sum=0;for(double x:w){if(!Double.isFinite(x)||x<0)throw new IllegalArgumentException("Balanced weights must be finite and non-negative");sum+=x;}if(Math.abs(sum-1.0)>1e-9)throw new IllegalArgumentException("Balanced weights must sum to 1.0; got "+sum);this.timeWeight=timeWeight;this.costWeight=costWeight;this.distanceWeight=distanceWeight;this.emissionWeight=emissionWeight;}
 public static BalancedRouteStrategy defaults(){return new BalancedRouteStrategy(.40,.30,.10,.20);}
 public RouteDecision selectRoute(List<Route> routes,DeliveryRequest request){List<Route> eligible=eligibleRoutes(routes,request);double mt=max(eligible,0),mc=max(eligible,1),md=max(eligible,2),me=max(eligible,3);Route r=eligible.stream().min(Comparator.comparingDouble(x->score(x,mt,mc,md,me))).orElseThrow();double s=score(r,mt,mc,md,me);return new RouteDecision(r,getStrategyName(),s,List.of(String.format(Locale.US,"Balanced score = time×%.2f + cost×%.2f + distance×%.2f + emissions×%.2f (each criterion normalized).",timeWeight,costWeight,distanceWeight,emissionWeight),String.format(Locale.US,"Normalized weighted score: %.4f (lower is better).",s),commonExplanation(r)));}
 private double max(List<Route> rs,int field){return rs.stream().mapToDouble(r->switch(field){case 0->r.effectiveMinutes();case 1->r.cost();case 2->r.distanceKm();default->r.carbonEmissionKg();}).max().orElse(0);}
 private double norm(double value,double max){return max==0?0:value/max;}
 private double score(Route r,double t,double c,double d,double e){return norm(r.effectiveMinutes(),t)*timeWeight+norm(r.cost(),c)*costWeight+norm(r.distanceKm(),d)*distanceWeight+norm(r.carbonEmissionKg(),e)*emissionWeight;}
 public String getStrategyName(){return "Balanced";}
}
