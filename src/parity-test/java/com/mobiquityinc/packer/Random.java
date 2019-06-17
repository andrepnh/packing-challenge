package com.mobiquityinc.packer;

import com.mobiquityinc.packer.algorithm.PackingAlgorithm;
import com.mobiquityinc.packer.core.Item;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Random {
  private static final java.util.Random RNG = new java.util.Random();

  public static Map.Entry<BigDecimal, Set<Item>> weightItemsPair() {
    return Map.entry(weight(), items());
  }

  public static BigDecimal weight() {
    return bigDecimal(Item.MAXIMUM_WEIGHT);
  }

  public static BigDecimal cost() {
    return bigDecimal(Item.MAXIMUM_COST);
  }

  public static Set<Item> items() {
    int amount = RNG.nextInt(PackingAlgorithm.MAXIMUM_CANDIDATES + 1);
    return Stream.generate(itemFactory())
        .limit(amount)
        .collect(Collectors.toSet());
  }

  public static Supplier<Item> itemFactory() {
    int[] index = {1};
    return () -> new Item(index[0]++, weight(), cost());
  }

  private static BigDecimal bigDecimal(BigDecimal maximum) {
    int i = RNG.nextInt(maximum.intValueExact() + 1);
    if (i != maximum.intValueExact()) {
      return new BigDecimal(i).add(new BigDecimal(RNG.nextDouble()));
    }
    return new BigDecimal(i);
  }

  private Random() { }
}
