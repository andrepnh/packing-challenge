package com.mobiquityinc.packer.core;

import static java.math.BigDecimal.ONE;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.mobiquityinc.exception.APIException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PackageTest {
  @Test
  void shouldNotAllowNullItems() {
    assertThrows(NullPointerException.class, () -> new Package(ONE, null));
  }

  @Test
  void shouldAllowAnyNumberOfItemsThatDoNotExceedPackageLimit() {
    double weightLimit = 100, itemWeight = 0.01;
    for (int itemAmount: new int[] {0, 1, (int)(weightLimit / itemWeight)}) {
      List<Item> items = IntStream.range(0, itemAmount)
          .mapToObj(zeroBasedIndex -> new Item(zeroBasedIndex + 1, new BigDecimal("0.01"), ONE))
          .collect(Collectors.toList());
      new Package(new BigDecimal(100), items);
    }
  }

  @Test
  void shouldNotAllowItemsWeightToExceedPackageLimit() {
    int itemAmount = 3, itemWeight = 3, packageLimit = itemWeight * itemAmount - 1;
    List<Item> items = IntStream.range(0, itemAmount)
        .mapToObj(zeroBasedIndex -> new Item(zeroBasedIndex + 1, new BigDecimal(itemWeight), ONE))
        .collect(Collectors.toList());
    assertThrows(
        IllegalArgumentException.class,
        () -> new Package(new BigDecimal(packageLimit), items));
  }

  @Test
  void shouldNotAllowWeightLimitAbove100() {
    assertThrows(
        APIException.class,
        () -> new Package(new BigDecimal("100.001"), Collections.emptyList()));
  }

  @ParameterizedTest
  @ValueSource(strings = {"0", "-0.001", "-1"})
  void shouldNotAllowZeroOrNegativeWeightLimit(String badWeightLimit) {
    assertThrows(
        APIException.class,
        () -> new Package(new BigDecimal(badWeightLimit), Collections.emptyList()));
  }

  @ParameterizedTest
  @ValueSource(strings = {"0.01", "1", "99.99", "100"})
  void shouldAllowValidWeightLimits(String weightLimit) {
    new Package(new BigDecimal(weightLimit), Collections.emptyList());
  }
}
