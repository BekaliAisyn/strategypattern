package com.aitu.delivery.selector;
import com.aitu.delivery.model.*; import com.aitu.delivery.strategy.*; import java.util.*;
/** Maps business priorities to algorithms; it does not implement route scoring. */
public final class StrategySelector {
 private final Map<DeliveryPriority,RouteStrategy> strategies=new EnumMap<>(DeliveryPriority.class);
 public StrategySelector(){this(BalancedRouteStrategy.defaults());}
 public StrategySelector(RouteStrategy standardStrategy){register(DeliveryPriority.URGENT,new FastestRouteStrategy());register(DeliveryPriority.ECONOMY,new CheapestRouteStrategy());register(DeliveryPriority.ECO,new EcoFriendlyRouteStrategy());register(DeliveryPriority.STANDARD,Objects.requireNonNull(standardStrategy));}
 public void register(DeliveryPriority priority,RouteStrategy strategy){strategies.put(Objects.requireNonNull(priority),Objects.requireNonNull(strategy));}
 public RouteStrategy select(DeliveryRequest request){if(request==null)throw new IllegalArgumentException("Delivery request is required");RouteStrategy result=strategies.get(request.priority());if(result==null)throw new IllegalStateException("No strategy registered for priority "+request.priority());return result;}
}
