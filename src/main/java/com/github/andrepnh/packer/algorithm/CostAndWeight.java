package com.github.andrepnh.packer.algorithm;

import com.github.andrepnh.packer.core.Item;
import com.google.common.base.MoreObjects;
import java.util.Objects;

class CostAndWeight {
  private final double cost;

  private final double weight;

  CostAndWeight(double cost, double weight) {
    this.cost = cost;
    this.weight = weight;
  }

  static CostAndWeight sumOf(Iterable<Item> items) {
    double totalCost = 0, totalWeight = 0;
    for (Item item: items) {
      totalCost += item.getCost();
      totalWeight += item.getWeight();
    }
    return new CostAndWeight(totalCost, totalWeight);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CostAndWeight that = (CostAndWeight) o;
    return Double.compare(that.cost, cost) == 0 &&
        Double.compare(that.weight, weight) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(cost, weight);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("cost", cost)
        .add("weight", weight)
        .toString();
  }

  double getCost() {
    return cost;
  }

  double getWeight() {
    return weight;
  }
}
