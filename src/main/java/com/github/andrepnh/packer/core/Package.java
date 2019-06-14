package com.github.andrepnh.packer.core;

import static com.github.andrepnh.packer.APIPreconditions.check;
import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import com.google.common.collect.ImmutableList;
import java.util.Collection;

public class Package {
  private final double weightLimit;

  private final ImmutableList<Item> items;

  public Package(double weightLimit, Collection<Item> items) {
    check(weightLimit > 0 && weightLimit <= 100,
        "Package weight limit must be between 0 (exclusive) and 100 (inclusive); got %.2f",
        weightLimit);
    double totalWeight = requireNonNull(items)
        .stream()
        .mapToDouble(Item::getWeight)
        .sum();
    // User input is ok, but the algorithm has made a mistake -- we won't throw APIException
    checkArgument(totalWeight <= weightLimit,
        "The total weight of the items exceed the package limit: %s > %s",
        totalWeight, weightLimit);
    this.weightLimit = weightLimit;
    this.items = ImmutableList.copyOf(items);
  }

  @Override
  public String toString() {
    return "Package{" +
        "weightLimit=" + weightLimit +
        ", items=" + items +
        '}';
  }

  public double getWeightLimit() {
    return weightLimit;
  }

  public ImmutableList<Item> getItems() {
    return items;
  }
}
