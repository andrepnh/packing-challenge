package com.github.andrepnh.packer.core;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import java.math.BigDecimal;

public class Input {
  private final BigDecimal weightLimit;

  private final ImmutableSet<Item> items;

  public Input(BigDecimal weightLimit, Iterable<Item> items) {
    this.weightLimit = weightLimit;
    this.items = ImmutableSet.copyOf(items);
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

  public ImmutableSet<Item> getItems() {
    return items;
  }
}
