package com.aitu.delivery;
import com.aitu.delivery.exception.NoSuitableRouteException;import com.aitu.delivery.model.*;import com.aitu.delivery.planner.RoutePlanner;import com.aitu.delivery.selector.StrategySelector;import com.aitu.delivery.strategy.*;import com.aitu.delivery.service.StrategyComparisonService;import org.junit.jupiter.api.Test;import java.util.*;import static org.junit.jupiter.api.Assertions.*;
class RouteStrategyTest {
 private final Location a=new Location("A",51,71), b=new Location("B",51.1,71.1);
 private Route route(String id,double km,int min,double cost,double co2,boolean available,int traffic,double safety){return new Route(id,a,b,km,min,cost,co2,available,traffic,safety);}
 private DeliveryRequest req(double budget,int deadline){return new DeliveryRequest("T",DeliveryPriority.STANDARD,budget,deadline,false,100,Set.of());}
 private List<Route> routes(){return List.of(route("A",10,20,500,5,true,5,7),route("B",12,24,300,2,true,0,9),route("C",8,30,400,0.5,true,0,8),route("D",5,15,1000,3,false,0,10));}
 @Test void fastestUsesTrafficAdjustedTime(){assertEquals("A",new FastestRouteStrategy().selectRoute(routes(),req(2000,60)).route().id());}
 @Test void cheapestSelectsMinimumCost(){assertEquals("B",new CheapestRouteStrategy().selectRoute(routes(),req(2000,60)).route().id());}
 @Test void ecoSelectsLowEmissionRoute(){assertEquals("C",new EcoFriendlyRouteStrategy().selectRoute(routes(),req(2000,60)).route().id());}
 @Test void balancedReturnsEligibleRoute(){assertTrue(Set.of("A","B","C").contains(BalancedRouteStrategy.defaults().selectRoute(routes(),req(2000,60)).route().id()));}
 @Test void fastestRespectsBudget(){assertNotEquals("D",new FastestRouteStrategy().selectRoute(routes(),req(450,60)).route().id());}
 @Test void unavailableRoutesAreRejected(){assertNotEquals("D",new CheapestRouteStrategy().selectRoute(routes(),req(2000,60)).route().id());}
 @Test void deadlineIsHardConstraint(){assertThrows(NoSuitableRouteException.class,()->new FastestRouteStrategy().selectRoute(routes(),req(2000,10)));}
 @Test void budgetIsHardConstraint(){assertEquals("C",new CheapestRouteStrategy().selectRoute(routes(),req(450,60)).route().id());}
 @Test void emissionLimitIsHardConstraint(){DeliveryRequest r=new DeliveryRequest("E",DeliveryPriority.ECO,2000,60,true,1,Set.of());assertEquals("C",new EcoFriendlyRouteStrategy().selectRoute(routes(),r).route().id());}
 @Test void prohibitedRouteIsRejected(){DeliveryRequest r=new DeliveryRequest("P",DeliveryPriority.STANDARD,2000,60,false,100,Set.of("B"));assertNotEquals("B",new CheapestRouteStrategy().selectRoute(routes(),r).route().id());}
 @Test void runtimeSwitchingUsesSamePlanner(){RoutePlanner p=new RoutePlanner(new FastestRouteStrategy());String first=p.plan(routes(),req(2000,60)).strategyName();p.setStrategy(new CheapestRouteStrategy());String second=p.plan(routes(),req(2000,60)).strategyName();assertEquals("Fastest",first);assertEquals("Cheapest",second);}
 @Test void selectorMapsUrgentToFastest(){assertInstanceOf(FastestRouteStrategy.class,new StrategySelector().select(DeliveryRequest.standard("1",DeliveryPriority.URGENT,1000,60)));}
 @Test void selectorMapsEconomyToCheapest(){assertInstanceOf(CheapestRouteStrategy.class,new StrategySelector().select(DeliveryRequest.standard("1",DeliveryPriority.ECONOMY,1000,60)));}
 @Test void selectorMapsEcoToEcoStrategy(){assertInstanceOf(EcoFriendlyRouteStrategy.class,new StrategySelector().select(DeliveryRequest.standard("1",DeliveryPriority.ECO,1000,60)));}
 @Test void selectorMapsStandardToBalanced(){assertInstanceOf(BalancedRouteStrategy.class,new StrategySelector().select(DeliveryRequest.standard("1",DeliveryPriority.STANDARD,1000,60)));}
 @Test void balancedRejectsWeightsNotSummingToOne(){assertThrows(IllegalArgumentException.class,()->new BalancedRouteStrategy(.4,.4,.1,.2));}
 @Test void balancedRejectsNegativeWeight(){assertThrows(IllegalArgumentException.class,()->new BalancedRouteStrategy(.5,.6,-.1,0));}
 @Test void ecoRejectsInvalidWeights(){assertThrows(IllegalArgumentException.class,()->new EcoFriendlyRouteStrategy(.8,.3));}
 @Test void emptyRoutesFailExplicitly(){assertThrows(NoSuitableRouteException.class,()->new FastestRouteStrategy().selectRoute(List.of(),req(2000,60)));}
 @Test void noEligibleRouteFailsExplicitly(){assertThrows(NoSuitableRouteException.class,()->new CheapestRouteStrategy().selectRoute(routes(),req(10,5)));}
 @Test void fifthSafestStrategyChoosesHighestSafety(){assertEquals("B",new SafestRouteStrategy().selectRoute(routes(),req(2000,60)).route().id());}
 @Test void comparisonRunsMultipleStrategies(){var map=new StrategyComparisonService().compare(List.of(new FastestRouteStrategy(),new CheapestRouteStrategy()),routes(),req(2000,60));assertEquals(2,map.size());assertTrue(map.containsKey("Fastest"));}
 @Test void boundaryBudgetIsAccepted(){assertEquals("B",new CheapestRouteStrategy().selectRoute(routes(),req(300,60)).route().id());}
 @Test void negativeRouteCostIsRejected(){assertThrows(IllegalArgumentException.class,()->route("X",1,1,-1,0,true,0,5));}
 @Test void missingRequestIsRejected(){assertThrows(IllegalArgumentException.class,()->new FastestRouteStrategy().selectRoute(routes(),null));}
 @Test void nullPlannerStrategyIsRejected(){assertThrows(NullPointerException.class,()->new RoutePlanner(null));}
 @Test void effectiveMinutesIncludesTraffic(){assertEquals(25,route("A",1,20,1,1,true,5,5).effectiveMinutes());}
 @Test void comparisonHandlesOnlyEligibleResults(){var map=new StrategyComparisonService().compare(List.of(new FastestRouteStrategy(),new CheapestRouteStrategy()),routes(),req(2000,60));assertEquals(2,map.size());}
}
