package com.mobiquityinc.packer;

import static com.google.common.base.Preconditions.checkState;
import static java.math.BigDecimal.ZERO;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.mobiquityinc.packer.algorithm.BruteForcePacking;
import com.mobiquityinc.packer.algorithm.MeetInTheMiddlePacking;
import com.mobiquityinc.packer.core.Item;
import com.mobiquityinc.packer.core.Package;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ParityTest {
  private static final String MINUTES_DURATION = "durationMinutes";
  private static final ExecutorService POOL = Executors.newSingleThreadExecutor();

  private LocalDateTime start;

  @BeforeEach
  void startTime() {
    start = LocalDateTime.now();
  }

  @Test
  void compareResults() {
    var bruteForce = new BruteForcePacking();
    var meetInTheMiddle = new MeetInTheMiddlePacking();
    int[] comparisons = {0};
    Stream.generate(Random::weightItemsPair)
        .takeWhile(pair -> !timeElapsed())
        .forEach(weightItemPair -> {
          BigDecimal packageWeightLimit = weightItemPair.getKey();
          Set<Item> candidateItems = weightItemPair.getValue();
          compare(
              () -> bruteForce.pack(packageWeightLimit, candidateItems),
              () -> meetInTheMiddle.pack(packageWeightLimit, candidateItems));
          comparisons[0]++;
        });
    System.out.format("=== Performed %d successful comparisons ===\n", comparisons[0]);
  }

  private void compare(Supplier<Package> algorithm1, Supplier<Package> algorithm2) {
    Future<Package> task = POOL.submit(algorithm1::get);
    Package package2 = algorithm2.get();
    try {
      assertEqualSolutions(task.get(), package2);
    } catch (InterruptedException | ExecutionException e) {
      throw new IllegalStateException(e);
    }
  }

  private void assertEqualSolutions(Package package1, Package package2) {
    assertEquals(package1.getWeightLimit(), package2.getWeightLimit());
    // We compare using the total cost and weight because there's a change the random data could
    // create input with multiple, equal solutions
    assertEquals(
        sum(package1.getItems(), Item::getCost),
        sum(package2.getItems(), Item::getCost));
    assertEquals(
        sum(package1.getItems(), Item::getWeight),
        sum(package2.getItems(), Item::getWeight));
  }

  private BigDecimal sum(Collection<Item> items, Function<Item, BigDecimal> getter) {
    return items.stream().map(getter).reduce(ZERO, BigDecimal::add);
  }

  public boolean timeElapsed() {
    int minutes = Optional.ofNullable(System.getProperty(MINUTES_DURATION))
        .filter(Predicate.not(String::isBlank))
        .map(Integer::parseInt)
        .orElse(1);
    checkState(minutes > 0);
    return Duration.between(start, LocalDateTime.now())
        .compareTo(Duration.ofMinutes(minutes)) > 0;
  }
}
