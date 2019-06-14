package com.github.andrepnh.packer.core;

import static com.github.andrepnh.packer.APIPreconditions.check;

import com.github.andrepnh.exception.APIException;

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
  public String toString() {
    return "Item{" +
        "index=" + index +
        ", weight=" + weight +
        ", cost=" + cost +
        '}';
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
