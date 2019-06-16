package com.github.andrepnh.packer.algorithm;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.function.Supplier;

final class PowerSets {

  /**
   * Finds all subsets of a set, including the empty set and itself. For further details, see:
   * https://codereview.stackexchange.com/a/164651
   *
   * @param set the set
   * @param powerSetSupplier a function that will create the power set given its capacity
   * @param subsetSupplier a function used to instantiate each subset
   * @param <P> power set type, can be a collection
   * @param <S> subset type
   * @param <E> element type
   *
   * @return the power set
   */
  static <P extends Collection<S>, S extends Set<E>, E> P find(
      Set<E> set, IntFunction<P> powerSetSupplier, Supplier<S> subsetSupplier) {
    // To make sure we don't overflow int
    checkArgument(set.size() <= 31,
        "Cannot find power set of a set with more than 31 elements; got %d",
        set.size());
    var elements = new ArrayList<>(set);
    int subsetCount = 1 << elements.size();
    P powerSet = powerSetSupplier.apply(subsetCount);

    for (int subsetNumber = 0; subsetNumber < subsetCount; subsetNumber++) {
      S subset = subsetSupplier.get();
      for (int index = 0; index < elements.size(); index++) {
        int mask = 1 << index;
        if ((subsetNumber & mask) != 0) {
          subset.add(elements.get(index));
        }
      }
      powerSet.add(subset);
    }

    return powerSet;
  }

  private PowerSets() {}
}
