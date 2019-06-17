package com.mobiquityinc.packer.algorithm;

import static java.math.BigDecimal.ZERO;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.mobiquityinc.packer.core.Item;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

class CostAndWeightTest {
  @Test
  void sumOfShouldSumBothWeightAndCost() {
    int costMultiplier = 2;
    List<Item> items = IntStream.range(1, 30)
        .mapToObj(val -> new Item(val, new BigDecimal(val), new BigDecimal(val * costMultiplier)))
        .collect(Collectors.toList());
    var baseSum = items.stream()
        .map(Item::getWeight)
        .reduce(ZERO, BigDecimal::add);

    var costAndWeight = CostAndWeight.sumOf(items);
    assertEquals(baseSum.multiply(new BigDecimal(costMultiplier)), costAndWeight.getCost());
    assertEquals(baseSum, costAndWeight.getWeight());
  }
}
