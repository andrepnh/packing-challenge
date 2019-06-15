package com.github.andrepnh.packer.core;

import static com.github.andrepnh.packer.APIPreconditions.check;

import com.github.andrepnh.exception.APIException;
import com.google.common.base.MoreObjects;
import java.util.Objects;

public class Item {
  private final int index;

  private final double weight;

  private final double cost;

  public Item(int index, double weight, double cost) throws APIException {
    check(index > 0, "Index should be greater than 0; got %d", index);
    check(weight >= 0 && weight <= 100,
        "Weight must be between 0 and 100 (inclusive); got %.2f",
        weight);
    check(cost >= 0 && cost <= 100,
        "Cost must be between 0 and 100 (inclusive); got %.2f",
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
        Double.compare(item.weight, weight) == 0 &&
        Double.compare(item.cost, cost) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(index, weight, cost);
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

  public double getWeight() {
    return weight;
  }

  public double getCost() {
    return cost;
  }
}
