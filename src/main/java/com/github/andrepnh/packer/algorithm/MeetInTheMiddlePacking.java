package com.github.andrepnh.packer.algorithm;

import static java.math.BigDecimal.ZERO;
import static java.util.Objects.requireNonNull;

import com.github.andrepnh.packer.core.Item;
import com.github.andrepnh.packer.core.Package;
import com.google.common.base.MoreObjects;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Spliterator;

/**
 * A common solution to the 0-1 knapsack problem that is relatively simple and can deal better with
 * non-integer weights. For further details see:
 * https://en.wikipedia.org/wiki/Knapsack_problem#Meet-in-the-middle
 */
public class MeetInTheMiddlePacking implements PackingAlgorithm {
  private static final BigDecimal MAXIMUM_ITEM_SET_COST = Item.MAXIMUM_COST
      .multiply(new BigDecimal(PackingAlgorithm.MAXIMUM_CANDIDATES));

  private static final Comparator<ItemSet> COMPARATOR_BY_COST_AND_REVERSED_WEIGHT = Comparator
      .comparing(ItemSet::getTotalCost)
      .thenComparing(Comparator
          .comparing(ItemSet::getTotalWeight)
          .reversed());

  @Override
  public Package pack(BigDecimal weightLimit, Set<Item> candidateItems) {
    Map.Entry<Set<Item>, Set<Item>> sets = splitInHalf(candidateItems);
    HashSet<ItemSet> powerSet1 = PowerSets
        .find(sets.getKey(), Sets::newHashSetWithExpectedSize, ItemSet::new);
    ArrayList<ItemSet> powerSet2 = PowerSets
        .find(sets.getValue(), Lists::newArrayListWithCapacity, ItemSet::new);
    powerSet2.sort(COMPARATOR_BY_COST_AND_REVERSED_WEIGHT);

    var bestCostAndWeight = new CostAndWeight(ZERO, Item.MAXIMUM_WEIGHT);
    Set<Item> bestCombination = new HashSet<>();
    for (ItemSet items1: powerSet1) {
      ItemSet items2 = findHighestCostSubsetBelowWeightLimit(items1, powerSet2, weightLimit);
      var combinedCostAndWeight = items1.getCostAndWeight().add(items2.getCostAndWeight());
      if (combinedCostAndWeight.fits(weightLimit)
          && combinedCostAndWeight.isHigherCostOrSameCostButLowerWeight(bestCostAndWeight)) {
        bestCostAndWeight = combinedCostAndWeight;
        bestCombination = Sets.union(items1, items2);
      }
    }

    return new Package(weightLimit, bestCombination);
  }

  private ItemSet findHighestCostSubsetBelowWeightLimit(
      ItemSet set, List<ItemSet> options, BigDecimal limit) {
    var remainingWeight = limit.subtract(set.totalWeight);
    if (remainingWeight.signum() <= 0) {
      return new ItemSet();
    }
    var dummySet = new DummyItemSet(remainingWeight, MAXIMUM_ITEM_SET_COST);
    int result = Collections.binarySearch(options, dummySet, COMPARATOR_BY_COST_AND_REVERSED_WEIGHT);
    if (result > 0) {
      return options.get(result);
    } else {
      // Check binarySearch javadoc to understand why this would be the index of the dummy set
      var dummySetIndexIfInserted = Math.abs(result + 1);
      var firstIndexToSearch = dummySetIndexIfInserted - 1;
      return rewindToFirstFit(options, firstIndexToSearch, remainingWeight);
    }
  }

  private ItemSet rewindToFirstFit(List<ItemSet> options, int index, BigDecimal weightLimit) {
    if (index <= 0) {
      return new ItemSet();
    }
    // The options were sorted by cost and then by weight, so the first fit we find will already
    // have the highest cost.
    for (; index >= 0; index--) {
      var currentSet = options.get(index);
      if (currentSet.getTotalWeight().compareTo(weightLimit) <= 0) {
        return currentSet;
      }
    }
    return new ItemSet();
  }

  private Entry<Set<Item>, Set<Item>> splitInHalf(Set<Item> set) {
    int half = set.size() / 2;
    Set<Item> firstHalf = Sets.newHashSetWithExpectedSize(half),
        secondHalf = Sets.newHashSetWithExpectedSize(set.size() - half);
    int index = 0;
    for (var item: set) {
      if (index++ < half) {
        firstHalf.add(item);
      } else {
        secondHalf.add(item);
      }
    }
    return Map.entry(firstHalf, secondHalf);
  }

  /**
   * A dummy item set used only for binary searches. Doesn't really store any items, but we have
   * control over its total weight and cost.
   */
  private static class DummyItemSet extends ItemSet {
    private final CostAndWeight totalWeightAndCost;

    public DummyItemSet(BigDecimal totalWeight, BigDecimal totalCost) {
      this.totalWeightAndCost = new CostAndWeight(totalCost, totalWeight);
    }

    @Override
    public CostAndWeight getCostAndWeight() {
      return totalWeightAndCost;
    }

    @Override
    public BigDecimal getTotalWeight() {
      return totalWeightAndCost.getWeight();
    }

    @Override
    public BigDecimal getTotalCost() {
      return totalWeightAndCost.getCost();
    }
  }

  /**
   * A specialized set of items that keeps track of total weight and cost.
   * IMPORTANT: items cannot be removed from the set.
   */
  private static class ItemSet implements Set<Item> {
    private final Set<Item> delegate;

    private BigDecimal totalWeight = ZERO;

    private BigDecimal totalCost = ZERO;

    public ItemSet() {
      this.delegate = new HashSet<>();
    }

    public ItemSet(int capacity) {
      this.delegate = Sets.newHashSetWithExpectedSize(capacity);
    }

    public static ItemSet of(Item item) {
      var set = new ItemSet();
      set.add(item);
      return set;
    }

    public CostAndWeight getCostAndWeight() {
      return new CostAndWeight(totalCost, totalWeight);
    }

    public BigDecimal getTotalWeight() {
      return totalWeight;
    }

    public BigDecimal getTotalCost() {
      return totalCost;
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this)
          .add("size", size())
          .add("totalWeight", totalWeight)
          .add("totalCost", totalCost)
          .toString();
    }

    @Override
    public int size() {
      return delegate.size();
    }

    @Override
    public boolean isEmpty() {
      return delegate.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
      return delegate.contains(o);
    }

    @Override
    public Iterator<Item> iterator() {
      return Iterators.unmodifiableIterator(delegate.iterator());
    }

    @Override
    public Object[] toArray() {
      return delegate.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
      return delegate.toArray(a);
    }

    @Override
    public boolean add(Item item) {
      var added = delegate.add(item);
      totalCost = totalCost.add(item.getCost());
      totalWeight = totalWeight.add(item.getWeight());
      return added;
    }

    @Override
    public boolean remove(Object o) {
      throw new UnsupportedOperationException("Items cannot be removed");
    }

    @Override
    public boolean containsAll(Collection<?> c) {
      return delegate.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Item> c) {
      var changed = false;
      for (var item: c) {
        changed = changed || add(item);
      }
      return changed;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
      throw new UnsupportedOperationException("Items cannot be removed");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
      throw new UnsupportedOperationException("Items cannot be removed");
    }

    @Override
    public void clear() {
      throw new UnsupportedOperationException("Items cannot be removed");
    }

    @Override
    public boolean equals(Object o) {
      return delegate.equals(o);
    }

    @Override
    public int hashCode() {
      return delegate.hashCode();
    }

    @Override
    public Spliterator<Item> spliterator() {
      return delegate.spliterator();
    }
  }
}
