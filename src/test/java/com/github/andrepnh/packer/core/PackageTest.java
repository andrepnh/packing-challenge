package com.github.andrepnh.packer.core;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.andrepnh.exception.APIException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

public class PackageTest {
  @Test
  public void shouldNotAllowNullItems() {
    assertThrows(NullPointerException.class, () -> new Package(1, null));
  }

  @Test
  public void shouldAllowAnyNumberOfItemsThatDoNotExceedPackageLimit() {
    double weightLimit = 100, itemWeight = 0.01;
    for (int itemAmount: new int[] {0, 1, (int)(weightLimit / itemWeight)}) {
      List<Item> items = IntStream.range(0, itemAmount)
          .mapToObj(zeroBasedIndex -> new Item(zeroBasedIndex + 1, 0.01, 1))
          .collect(Collectors.toList());
      new Package(100, items);
    }
  }

  @Test
  public void shouldNotAllowItemsWeightToExceedPackageLimit() {
    int itemAmount = 3, itemWeight = 3, packageLimit = itemWeight * itemAmount - 1;
    List<Item> items = IntStream.range(0, itemAmount)
        .mapToObj(zeroBasedIndex -> new Item(zeroBasedIndex + 1, itemWeight, 1))
        .collect(Collectors.toList());
    assertThrows(IllegalArgumentException.class, () -> new Package(packageLimit, items));
  }

  @Test
  public void shouldNotAllowWeightLimitAbove100() {
    assertThrows(APIException.class, () -> new Package(100.001, Collections.emptyList()));
  }

  @Test
  public void shouldNotAllowZeroOrNegativeWeightLimit() {
    for (double badWeightLimit: new double[] {0, -0.001, -1}) {
      assertThrows(APIException.class, () -> new Package(badWeightLimit, Collections.emptyList()));
    }
  }

  @Test
  public void shouldAllowValidWeightLimits() {
    for (double weightLimit: new double[] {0.01, 1, 99.99, 100}) {
      new Package(weightLimit, Collections.emptyList());
    }
  }
}
