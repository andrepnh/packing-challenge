package com.mobiquityinc.packer.algorithm;

import static com.mobiquityinc.packer.APIPreconditions.check;

import com.mobiquityinc.packer.core.Item;
import com.mobiquityinc.packer.core.Package;
import java.math.BigDecimal;
import java.util.Set;

public interface PackingAlgorithm {
  int MAXIMUM_CANDIDATES = 15;

  Package pack(BigDecimal weightLimit, Set<Item> candidateItems);

  default void checkCandidatesAmount(Set<Item> candidateItems) {
    check(candidateItems.size() <= MAXIMUM_CANDIDATES,
        "Can only consider %d items at a time; got %d",
        MAXIMUM_CANDIDATES, candidateItems.size());
  }

  /**
   * To ensure we don't run expensive algorithms that will end up throwing an exception at the
   * final step because of {@link Package} weight limit violations.
   */
  default void checkWeightLimit(BigDecimal weightLimit) {
    check(weightLimit.compareTo(Package.MAXIMUM_LIMIT) <= 0,
        "%s exceed package weight limit of %s",
        weightLimit, Package.MAXIMUM_LIMIT);
  }
}
