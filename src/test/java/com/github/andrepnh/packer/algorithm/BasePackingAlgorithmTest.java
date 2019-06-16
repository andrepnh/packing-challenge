package com.github.andrepnh.packer.algorithm;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.andrepnh.packer.core.Item;
import com.github.andrepnh.packer.core.Package;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
class BasePackingAlgorithmTest {
  private final PackingAlgorithm algorithm;

  BasePackingAlgorithmTest(PackingAlgorithm algorithm) {
    this.algorithm = requireNonNull(algorithm);
  }

  @Test
  void packageShouldBeEmptyIfAllItemsAlreadyExceedTheLimit() {
    Function<CostAndWeight, Item> itemFactory = itemFactory();
    var packageLimit = 10D;
    var items = Sets.newHashSet(
        itemFactory.apply(costWeight(10, packageLimit + 10)),
        itemFactory.apply(costWeight(100, packageLimit + 1)),
        itemFactory.apply(costWeight(99, packageLimit + 0.01)));

    Package pack = algorithm.pack(packageLimit, items);

    assertEquals(Collections.emptyList(), pack.getItems());
  }

  @Test
  void shouldPackASingleItemIfItIsTheOnlyOneToFitThePackage() {
    Function<CostAndWeight, Item> itemFactory = itemFactory();
    var packageLimit = 10D;
    var bestItem = itemFactory.apply(costWeight(1, packageLimit));
    var items = Sets.newHashSet(
        bestItem,
        itemFactory.apply(costWeight(50, packageLimit + 1)),
        itemFactory.apply(costWeight(50, packageLimit + 0.01)));

    Package pack = algorithm.pack(packageLimit, items);

    assertEquals(Collections.singletonList(bestItem), pack.getItems());
  }

  @Test
  void shouldPackASingleItemsIfItYieldsTheBestCost() {
    Function<CostAndWeight, Item> itemFactory = itemFactory();
    var packageLimit = 4D;
    var bestItem = itemFactory.apply(costWeight(100, packageLimit));
    var items = Sets.newHashSet(
        bestItem,
        itemFactory.apply(costWeight(50, 1)),
        itemFactory.apply(costWeight(49.99, 1)));

    Package pack = algorithm.pack(packageLimit, items);

    assertEquals(Collections.singletonList(bestItem), pack.getItems());
  }

  @Test
  void shouldPackACombinationOfItemsIfTheyYieldTheBestCost() {
    Function<CostAndWeight, Item> itemFactory = itemFactory();
    var packageLimit = 3D;
    var bestCombination = Sets.newHashSet(
        itemFactory.apply(costWeight(33.34, packageLimit / 3)),
        itemFactory.apply(costWeight(33.34, packageLimit / 3)),
        itemFactory.apply(costWeight(33.33, packageLimit / 3)));
    var items = Sets.union(
        bestCombination,
        Collections.singleton(itemFactory.apply(costWeight(100, packageLimit))));

    Package pack = algorithm.pack(packageLimit, items);

    assertEquals(bestCombination, new HashSet<>(pack.getItems()));
  }

  @Test
  void shouldFavorTheCombinationWithTheLowestWeightIfMultipleOnesHaveTheSameCost() {
    Function<CostAndWeight, Item> itemFactory = itemFactory();
    double packageLimit = 12, bestCost = 120;
    var combination1 = Sets.newHashSet(
        itemFactory.apply(costWeight(bestCost / 2, packageLimit / 2)),
        itemFactory.apply(costWeight(bestCost / 2, packageLimit / 2)));
    var combination3 = Sets.newHashSet(
        itemFactory.apply(costWeight(bestCost / 4, packageLimit / 4)),
        itemFactory.apply(costWeight(bestCost / 4, packageLimit / 4)),
        itemFactory.apply(costWeight(bestCost / 4, packageLimit / 4)),
        itemFactory.apply(costWeight(bestCost / 4, packageLimit / 4)));
    // This combination has an higher cost/weight and can't fit more items once added
    var bestCombination = Sets.newHashSet(
        itemFactory.apply(costWeight(bestCost / 3, 3.5)),
        itemFactory.apply(costWeight(bestCost / 3, 3.5)),
        itemFactory.apply(costWeight(bestCost / 3, 3.5)));

    Package pack = algorithm.pack(packageLimit,
        Sets.newHashSet(Iterables.concat(combination1, bestCombination, combination3)));

    assertEquals(bestCombination, new HashSet<>(pack.getItems()));
  }

  @Test
  void shouldNotFavorLowestWeightIfACombinationHasHighestCost() {
    Function<CostAndWeight, Item> itemFactory = itemFactory();
    double packageLimit = 12, bestCost = 120;
    var combination2 = Sets.newHashSet(
        itemFactory.apply(costWeight(bestCost / 3, packageLimit / 3)),
        itemFactory.apply(costWeight(bestCost / 3, packageLimit / 3)),
        itemFactory.apply(costWeight(bestCost / 3, packageLimit / 3)));
    var combination3 = Sets.newHashSet(
        itemFactory.apply(costWeight(bestCost / 4, packageLimit / 4)),
        itemFactory.apply(costWeight(bestCost / 4, packageLimit / 4)),
        itemFactory.apply(costWeight(bestCost / 4, packageLimit / 4)),
        itemFactory.apply(costWeight(bestCost / 4, packageLimit / 4)));
    // This combination has an higher cost/weight and can't fit more items once added
    var bestCombination = Sets.newHashSet(
        itemFactory.apply(costWeight((bestCost / 2) + 0.01, packageLimit / 2)),
        itemFactory.apply(costWeight(bestCost / 2 + 0.01, packageLimit / 2)));

    Package pack = algorithm.pack(packageLimit,
        Sets.newHashSet(Iterables.concat(bestCombination, combination2, combination3)));

    assertEquals(bestCombination, new HashSet<>(pack.getItems()));
  }

  @Test
  void shouldPackAllItemsIfTheyFitThePackage() {
    Function<CostAndWeight, Item> itemFactory = itemFactory();
    var packageLimit = 100D;
    Set<Item> allItems = IntStream.rangeClosed(1, 15)
        .mapToObj(i -> itemFactory.apply(costWeight(1, 1)))
        .collect(Collectors.toSet());

    Package pack = algorithm.pack(packageLimit, allItems);

    assertEquals(allItems, new HashSet<>(pack.getItems()));
  }

  private Function<CostAndWeight, Item> itemFactory() {
    // Working around "final" constraint of lambda expression
    int[] serial = {1};
    return costWeight -> new Item(serial[0]++, costWeight.getCost(), costWeight.getWeight());
  }

  // Convenience method to make data setup slight more readable
  private CostAndWeight costWeight(double cost, double weight) {
    return new CostAndWeight(cost, weight);
  }
}
