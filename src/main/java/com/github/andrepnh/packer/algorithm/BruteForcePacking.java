package com.github.andrepnh.packer.algorithm;

import static java.math.BigDecimal.ZERO;

import com.github.andrepnh.packer.core.Item;
import com.github.andrepnh.packer.core.Package;
import com.google.common.collect.Sets;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;

/**
 * Finds the best packing option by checking all possible combinations.
 */
public class BruteForcePacking implements PackingAlgorithm {
  @Override
  public Package pack(BigDecimal weightLimit, Set<Item> candidateItems) {
    var bestOption = Collections.<Item>emptySet();
    var bestCostAndWeight = new CostAndWeight(ZERO, Item.MAXIMUM_WEIGHT);

    for (Set<Item> combination: Sets.powerSet(candidateItems)) {
      var combinationCostAndWeight = CostAndWeight.sumOf(combination);
      if (!combinationCostAndWeight.fits(weightLimit)) {
        continue;
      }
      if (combinationCostAndWeight.isHigherCostOrSameCostButLowerWeight(bestCostAndWeight)) {
        bestOption = combination;
        bestCostAndWeight = combinationCostAndWeight;
      }
    }

    return new Package(weightLimit, bestOption);
  }

}
