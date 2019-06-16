package com.github.andrepnh.packer.core;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;

public class Input {
  private final int weightLimit;

  private final ImmutableSet<Item> items;

  public Input(int weightLimit, Iterable<Item> items) {
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

  public int getWeightLimit() {
    return weightLimit;
  }

  public ImmutableSet<Item> getItems() {
    return items;
  }
}
