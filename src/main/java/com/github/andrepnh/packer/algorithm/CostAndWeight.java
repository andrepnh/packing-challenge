package com.github.andrepnh.packer.algorithm;

import static java.math.BigDecimal.ZERO;

import com.github.andrepnh.packer.core.Item;
import com.google.common.base.MoreObjects;
import java.math.BigDecimal;
import java.util.Objects;

class CostAndWeight {
  private final BigDecimal cost;

  private final BigDecimal weight;

  CostAndWeight(BigDecimal cost, BigDecimal weight) {
    this.cost = cost;
    this.weight = weight;
  }

  static CostAndWeight sumOf(Iterable<Item> items) {
    BigDecimal totalCost = ZERO, totalWeight = ZERO;
    for (Item item: items) {
      totalCost = totalCost.add(item.getCost());
      totalWeight = totalWeight.add(item.getWeight());
    }
    return new CostAndWeight(totalCost, totalWeight);
  }

  CostAndWeight add(CostAndWeight that) {
    return new CostAndWeight(this.cost.add(that.cost), this.weight.add(that.weight));
  }

  boolean fits(BigDecimal weightLimit) {
    return this.weight.compareTo(weightLimit) <= 0;
  }

  boolean isHigherCostOrSameCostButLowerWeight(CostAndWeight other) {
    var higherCost = this.cost.compareTo(other.cost) > 0;
    var sameCostLowerWeight = this.cost.compareTo(other.cost) == 0
        && this.weight.compareTo(other.weight) < 0;
    return higherCost || sameCostLowerWeight;
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
    return that.cost.compareTo(cost) == 0 &&
        that.weight.compareTo(weight) == 0;
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

  BigDecimal getCost() {
    return cost;
  }

  BigDecimal getWeight() {
    return weight;
  }
}
