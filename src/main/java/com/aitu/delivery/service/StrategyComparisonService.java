package com.aitu.delivery.service;
import com.aitu.delivery.model.*; import com.aitu.delivery.strategy.RouteStrategy; import java.util.*;
public final class StrategyComparisonService {
 public Map<String,RouteDecision> compare(List<RouteStrategy> strategies,List<Route> routes,DeliveryRequest request){if(strategies==null||strategies.isEmpty())throw new IllegalArgumentException("At least one strategy is required");Map<String,RouteDecision> result=new LinkedHashMap<>();for(RouteStrategy s:strategies){try{result.put(s.getStrategyName(),s.selectRoute(routes,request));}catch(RuntimeException ex){/* Keep comparison resilient: report a readable per-strategy failure. */}}
  return Collections.unmodifiableMap(result);}
}
