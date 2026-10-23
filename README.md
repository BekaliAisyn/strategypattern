# Smart Delivery & Route Optimization Engine

**Assignment 5 — Strategy Pattern | Java 17+ | JUnit 5**

## 1. Problem and solution
A delivery system has several valid ways to choose a route. The Strategy Pattern puts each algorithm behind the `RouteStrategy` interface, so the algorithm can be replaced without changing `RoutePlanner`.

## 2. Architecture
- **Context:** `planner/RoutePlanner` — stores a `RouteStrategy` and delegates `plan()` to it.
- **Strategy abstraction:** `strategy/RouteStrategy`.
- **Concrete strategies:** `FastestRouteStrategy`, `CheapestRouteStrategy`, `EcoFriendlyRouteStrategy`, `BalancedRouteStrategy`, and the added `SafestRouteStrategy`.
- **StrategySelector:** maps delivery priority to a strategy; it does not score routes.
- **Domain model:** `Route`, `Location`, `DeliveryRequest`, `RouteDecision`, `DeliveryPriority`.
- **Comparison:** `StrategyComparisonService` runs several strategies for the same request.

## 3. Requirements and algorithm details
All strategies first apply hard constraints: route availability, prohibited route IDs, budget, effective delivery deadline (estimated time + traffic penalty), and maximum emissions. If the candidate list is empty or all routes are filtered out, `NoSuitableRouteException` is thrown; the system does not silently return `null`.

- **Fastest:** minimizes `estimatedMinutes + trafficPenaltyMinutes`; cost is a tie-breaker.
- **Cheapest:** minimizes the total `Route.cost`. The model's cost represents the total delivery price, including any base, distance, priority and additional fees calculated upstream.
- **Eco-Friendly:** minimizes `normalized emissions × 0.80 + normalized distance × 0.20` by default. Both weights are configurable and must sum to 1.
- **Balanced:** minimizes a weighted sum of normalized time, cost, distance and emissions. Default weights are 0.40, 0.30, 0.10 and 0.20. The constructor rejects negative/non-finite weights and weights that do not sum to 1.0.
- **Safest (fifth strategy):** maximizes the route safety score from 0 to 10.

Normalization uses the maximum value among eligible routes for each metric. Lower score wins for Eco and Balanced. Zero maximum values normalize to zero to avoid division by zero.

## 4. Automatic selection
`StrategySelector` maps `URGENT → Fastest`, `ECONOMY → Cheapest`, `ECO → Eco-Friendly`, and `STANDARD → Balanced`. The mapping can be extended through `register()`.

## 5. Runtime switching
The demo uses one `RoutePlanner` instance and calls `setStrategy()` repeatedly with different strategies. The comparison mode evaluates all five strategies against the same request and candidate list.

## 6. Run the application
Requirements: JDK 17+ and Maven 3.9+.

```bash
mvn clean test
mvn exec:java
```

On Windows PowerShell, run the same commands in the project directory. If Maven is not installed, install Maven 3.9+ and ensure `mvn -version` works. The project targets Java 17 and is compatible with newer JDKs.

## 7. Tests
`RouteStrategyTest` contains 28 JUnit 5 tests covering each strategy, runtime switching, automatic selection, hard constraints, budget, deadline, emissions, prohibited/unavailable routes, invalid weights, the fifth strategy, comparison mode, boundaries, invalid input and no-suitable-route failures.

## 8. UML
PlantUML sources are in `docs/uml/`. Open them in a PlantUML-compatible viewer to render the class and sequence diagrams.

## 9. Strategy vs Bridge
Strategy chooses one interchangeable algorithm for a task (here, how to select a route). Bridge separates two independent dimensions of abstraction and implementation so both can vary independently. This project uses Strategy because the main requirement is replacing the route-selection algorithm at runtime.

## 10. SOLID and Clean Code
- **SRP:** route selection algorithms, business-priority mapping, orchestration, and comparison have separate classes.
- **OCP:** new algorithms can implement `RouteStrategy`; `RoutePlanner` does not need modification. The selector can register a new mapping.
- **DIP:** `RoutePlanner` depends on the `RouteStrategy` abstraction, not concrete strategies.
- **Validation:** domain constructors reject invalid values; explicit exceptions communicate invalid requests and missing feasible routes.

## 11. Current limitations
The sample routes and scores are demonstration data, not live traffic or map-service results. `Route.cost` is assumed to contain the fully calculated delivery price. The comparison service omits strategies that fail to find a route rather than returning partial failure objects; production code could report a status per strategy. The safety score is illustrative and must be sourced from a real safety model in production.

## 12. Git history
The repository is structured for incremental development. See the commit history for the staged implementation and documentation changes.
