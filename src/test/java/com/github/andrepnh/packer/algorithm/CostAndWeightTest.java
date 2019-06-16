package com.github.andrepnh.packer.algorithm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.andrepnh.packer.core.Item;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

class CostAndWeightTest {
  @Test
  void sumOfShouldSumBothWeightAndCost() {
    int costMultiplier = 2;
    List<Item> items = IntStream.range(1, 30)
        .mapToObj(val -> new Item(val, val * costMultiplier, val))
        .collect(Collectors.toList());
    var expectedSum = items.stream().mapToDouble(Item::getWeight).sum();

    CostAndWeight costAndWeight = CostAndWeight.sumOf(items);
    assertEquals(expectedSum * costMultiplier, costAndWeight.getCost());
    assertEquals(expectedSum, costAndWeight.getWeight());
  }
}
