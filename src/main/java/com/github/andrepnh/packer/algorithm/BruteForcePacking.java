package com.github.andrepnh.packer.algorithm;

import com.github.andrepnh.packer.core.Item;
import com.github.andrepnh.packer.core.Package;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Set;

/**
 * Finds the best packing option by checking all possibilities.
 */
public class BruteForcePacking implements PackingAlgorithm {
  @Override
  public Package pack(double weightLimit, Set<Item> candidateItems) {
    var bestOption = Collections.<Item>emptySet();
    var bestCostAndWeight = new CostAndWeight(-1, Double.MAX_VALUE);

    for (int combinationSize = 1; combinationSize <= candidateItems.size(); combinationSize++) {
      Set<Set<Item>> combinations = Sets.combinations(candidateItems, combinationSize);
      for (Set<Item> combination: combinations) {
        var combinationCostAndWeight = CostAndWeight.sumOf(combination);
        if (combinationCostAndWeight.getWeight() > weightLimit) {
          continue;
        }
        if (higherCostOrSameCostButLowerWeight(combinationCostAndWeight, bestCostAndWeight)) {
          bestOption = combination;
          bestCostAndWeight = combinationCostAndWeight;
        }
      }
    }

    return new Package(weightLimit, bestOption);
  }

  private boolean higherCostOrSameCostButLowerWeight(
      CostAndWeight costAndWeight, CostAndWeight bestCostAndWeight) {
    var higherCost = costAndWeight.getCost() > bestCostAndWeight.getCost();
    var sameCostLowerWeight = costAndWeight.getCost() == bestCostAndWeight.getCost()
        && costAndWeight.getWeight() < bestCostAndWeight.getWeight();
    return higherCost || sameCostLowerWeight;
  }

}
