package com.mobiquityinc.packer.algorithm;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.common.collect.Lists;
import com.mobiquityinc.exception.APIException;
import com.mobiquityinc.packer.core.Item;
import com.mobiquityinc.packer.core.Package;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
class BasePackingAlgorithmTest {
  final PackingAlgorithm algorithm;

  private Function<CostAndWeight, Item> itemFactory;

  BasePackingAlgorithmTest(PackingAlgorithm algorithm) {
    this.algorithm = requireNonNull(algorithm);
  }

  @BeforeEach
  void setup() {
    itemFactory = itemFactory();
  }

  @Test
  void shouldNotAllowMoreItemsThanPermitted() {
    var limit = 15;
    var items = Stream
        .generate(() -> itemFactory.apply(costWeight(1, 1)))
        .limit(limit + 1)
        .collect(Collectors.toSet());

    assertThrows(APIException.class, () -> algorithm.pack(TEN, items));
  }

  @Test
  void packageShouldBeEmptyIfAllItemsAlreadyExceedTheLimit() {
    var packageLimit = TEN;
    var items = Sets.newHashSet(
        itemFactory.apply(costWeight(TEN, packageLimit.add(TEN))),
        itemFactory.apply(costWeight(100, TEN.add(ONE))),
        itemFactory.apply(costWeight(99, packageLimit.add(new BigDecimal("0.01")))));

    Package pack = algorithm.pack(packageLimit, items);

    assertEquals(Collections.emptyList(), pack.getItems());
  }

  @Test
  void shouldPackASingleItemIfItIsTheOnlyOneToFitThePackage() {
    var packageLimit = TEN;
    var bestItem = itemFactory.apply(costWeight(ONE, packageLimit));
    var items = Sets.newHashSet(
        bestItem,
        itemFactory.apply(costWeight(50, packageLimit.add(ONE))),
        itemFactory.apply(costWeight(50, packageLimit.add(new BigDecimal("0.01")))));

    Package pack = algorithm.pack(packageLimit, items);

    assertEquals(Collections.singletonList(bestItem), pack.getItems());
  }

  @Test
  void shouldPackASingleItemsIfItYieldsTheBestCost() {
    var packageLimit = new BigDecimal(4);
    var bestItem = itemFactory.apply(costWeight(100, packageLimit));
    var items = Sets.newHashSet(
        bestItem,
        itemFactory.apply(costWeight(50, 1)),
        itemFactory.apply(costWeight("49.99", 1)));

    Package pack = algorithm.pack(packageLimit, items);

    assertEquals(Collections.singletonList(bestItem), pack.getItems());
  }

  @Test
  void shouldPackACombinationOfItemsIfTheyYieldTheBestCost() {
    var packageLimit = new BigDecimal(3);
    BigDecimal cost = new BigDecimal("33.34"), weight = packageLimit.divide(new BigDecimal(3));
    var bestCombination = Sets.newHashSet(
        itemFactory.apply(costWeight(cost, weight)),
        itemFactory.apply(costWeight(cost, weight)),
        itemFactory.apply(costWeight(cost, weight)));
    var items = Sets.union(
        bestCombination,
        Collections.singleton(itemFactory.apply(costWeight(100, packageLimit))));

    Package pack = algorithm.pack(packageLimit, items);

    assertEquals(bestCombination, new HashSet<>(pack.getItems()));
  }

  @Test
  @SuppressWarnings("BigDecimalMethodWithoutRoundingCalled") // All division are exact
  void shouldFavorTheCombinationWithTheLowestWeightIfMultipleOnesHaveTheSameCost() {
    BigDecimal packageLimit = new BigDecimal(12),
        bestCost = new BigDecimal(120);

    BigDecimal sharedCost = bestCost.divide(new BigDecimal(2)),
        sharedWeight = packageLimit.divide(new BigDecimal(2));
    var combination1 = Sets.newHashSet(
        itemFactory.apply(costWeight(sharedCost, sharedWeight)),
        itemFactory.apply(costWeight(sharedCost, sharedWeight)));

    sharedCost = bestCost.divide(new BigDecimal(4));
    sharedWeight = packageLimit.divide(new BigDecimal(4));
    var combination3 = Sets.newHashSet(
        itemFactory.apply(costWeight(sharedCost, sharedWeight)),
        itemFactory.apply(costWeight(sharedCost, sharedWeight)),
        itemFactory.apply(costWeight(sharedCost, sharedWeight)),
        itemFactory.apply(costWeight(sharedCost, sharedWeight)));

    sharedCost = bestCost.divide(new BigDecimal(3));
    sharedWeight = new BigDecimal("3.5");
    // This is the best because it has an higher cost/weight and can't fit more items once added
    var bestCombination = Sets.newHashSet(
        itemFactory.apply(costWeight(sharedCost, sharedWeight)),
        itemFactory.apply(costWeight(sharedCost, sharedWeight)),
        itemFactory.apply(costWeight(sharedCost, sharedWeight)));

    Package pack = algorithm.pack(packageLimit,
        Sets.newHashSet(Iterables.concat(combination1, bestCombination, combination3)));

    assertEquals(bestCombination, new HashSet<>(pack.getItems()));
  }

  @Test
  @SuppressWarnings("BigDecimalMethodWithoutRoundingCalled") // All division are exact
  void shouldNotFavorLowestWeightIfACombinationHasHighestCost() {
    BigDecimal packageLimit = new BigDecimal(12),
        bestCost = new BigDecimal(120);

    BigDecimal sharedCost = bestCost.divide(new BigDecimal(3)),
        sharedWeight = packageLimit.divide(new BigDecimal(3));
    var combination2 = Sets.newHashSet(
        itemFactory.apply(costWeight(sharedCost, sharedWeight)),
        itemFactory.apply(costWeight(sharedCost, sharedWeight)),
        itemFactory.apply(costWeight(sharedCost, sharedWeight)));

    sharedCost = bestCost.divide(new BigDecimal(4));
    sharedWeight = packageLimit.divide(new BigDecimal(4));
    var combination3 = Sets.newHashSet(
        itemFactory.apply(costWeight(sharedCost, sharedWeight)),
        itemFactory.apply(costWeight(sharedCost, sharedWeight)),
        itemFactory.apply(costWeight(sharedCost, sharedWeight)),
        itemFactory.apply(costWeight(sharedCost, sharedWeight)));

    sharedCost = bestCost.divide(new BigDecimal(2)).add(new BigDecimal("0.01"));
    sharedWeight = packageLimit.divide(new BigDecimal(2));
    // This is the best because it has an higher cost/weight and can't fit more items once added
    var bestCombination = Sets.newHashSet(
        itemFactory.apply(costWeight(sharedCost, sharedWeight)),
        itemFactory.apply(costWeight(sharedCost, sharedWeight)));

    Package pack = algorithm.pack(packageLimit,
        Sets.newHashSet(Iterables.concat(bestCombination, combination2, combination3)));

    assertEquals(bestCombination, new HashSet<>(pack.getItems()));
  }

  @Test
  void shouldPackAllItemsIfTheyFitThePackage() {
    var packageLimit = new BigDecimal(100);
    Set<Item> allItems = IntStream.rangeClosed(1, 3)
        .mapToObj(i -> itemFactory.apply(costWeight(1, 1)))
        .collect(Collectors.toSet());

    Package pack = algorithm.pack(packageLimit, allItems);

    assertEquals(allItems, new HashSet<>(pack.getItems()));
  }

  private Function<CostAndWeight, Item> itemFactory() {
    // Working around "final" constraint of lambda expression
    int[] serial = {1};
    return costWeight -> new Item(serial[0]++, costWeight.getWeight(), costWeight.getCost());
  }

  // Convenience methods to make data setup slight more readable
  private CostAndWeight costWeight(BigDecimal cost, BigDecimal weight) {
    return new CostAndWeight(cost, weight);
  }

  private CostAndWeight costWeight(String cost, int weight) {
    return new CostAndWeight(new BigDecimal(cost), new BigDecimal(weight));
  }

  private CostAndWeight costWeight(int cost, int weight) {
    return new CostAndWeight(new BigDecimal(cost), new BigDecimal(weight));
  }

  private CostAndWeight costWeight(int cost, BigDecimal weight) {
    return new CostAndWeight(new BigDecimal(cost), weight);
  }
}
