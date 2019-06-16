package com.github.andrepnh.packer.core;

import static com.github.andrepnh.packer.APIPreconditions.check;
import static java.math.BigDecimal.ZERO;
import static java.util.Objects.requireNonNull;

import com.github.andrepnh.exception.APIException;
import com.google.common.base.MoreObjects;
import java.math.BigDecimal;
import java.util.Objects;

public class Item {
  public static final BigDecimal MAXIMUM_WEIGHT = new BigDecimal("100");
  public static final BigDecimal MAXIMUM_COST = new BigDecimal("100");

  private final int index;

  private final BigDecimal cost;

  private final BigDecimal weight;

  public Item(int index, BigDecimal cost, BigDecimal weight) throws APIException {
    requireNonNull(cost);
    requireNonNull(weight);
    check(index > 0, "Index should be greater than 0; got %d", index);
    check(weight.compareTo(ZERO) >= 0 && weight.compareTo(MAXIMUM_WEIGHT) <= 0,
        "Weight must be between 0 and 100 (inclusive); got %s",
        weight);
    check(cost.compareTo(ZERO) >= 0 && cost.compareTo(MAXIMUM_COST) <= 0,
        "Cost must be between 0 and 100 (inclusive); got %s",
        cost);
    this.index = index;
    this.weight = weight;
    this.cost = cost;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Item item = (Item) o;
    return index == item.index &&
        cost.compareTo(item.cost) == 0 && // compareTo instead of equals to ignore scale differences
        weight.compareTo(item.weight) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(index, cost, weight);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("index", index)
        .add("weight", weight)
        .add("cost", cost)
        .toString();
  }

  public int getIndex() {
    return index;
  }

  public BigDecimal getWeight() {
    return weight;
  }

  public BigDecimal getCost() {
    return cost;
  }
}
