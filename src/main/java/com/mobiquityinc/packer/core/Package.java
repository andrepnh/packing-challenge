package com.mobiquityinc.packer.core;

import static com.google.common.base.Preconditions.checkArgument;
import static com.mobiquityinc.packer.APIPreconditions.check;
import static java.math.BigDecimal.ZERO;
import static java.util.Objects.requireNonNull;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import java.math.BigDecimal;
import java.util.Collection;

public class Package {
  public static final BigDecimal MAXIMUM_LIMIT = new BigDecimal(100);

  private final BigDecimal weightLimit;

  private final ImmutableList<Item> items;

  public Package(BigDecimal weightLimit, Collection<Item> items) {
    check(weightLimit.compareTo(ZERO) > 0 && weightLimit.compareTo(MAXIMUM_LIMIT) <= 0,
        "Package weight limit must be between 0 (exclusive) and 100 (inclusive); got %s",
        weightLimit);

    BigDecimal totalWeight = requireNonNull(items)
        .stream()
        .map(Item::getWeight)
        .reduce(ZERO, BigDecimal::add);
    // User input is ok, but the algorithm has made a mistake -- we won't throw APIException
    checkArgument(totalWeight.compareTo(weightLimit) <= 0,
        "The total weight of the items exceeded the package limit: %s > %s",
        totalWeight, weightLimit);

    this.weightLimit = weightLimit;
    this.items = ImmutableList.copyOf(items);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("weightLimit", weightLimit)
        .add("items", items)
        .toString();
  }

  public BigDecimal getWeightLimit() {
    return weightLimit;
  }

  public ImmutableList<Item> getItems() {
    return items;
  }
}
