package com.github.andrepnh.packer.core;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.andrepnh.exception.APIException;
import org.junit.jupiter.api.Test;

public class ItemTest {
  @Test
  public void shouldNotAllowZeroOrNegativeIndex() {
    for (int badIndex: new int[] {0, -1, Integer.MIN_VALUE}) {
      assertThrows(
          APIException.class,
          () -> new Item(badIndex, 1, 1),
          "Expected exception for index: " + badIndex);
    }
  }

  @Test
  public void shouldAllowZeroWeight() {
    new Item(1, 0,1);
  }

  @Test
  public void shouldNotAllowNegativeWeight() {
    assertThrows(APIException.class, () -> new Item(1, -0.001, 1));
  }

  @Test
  public void shouldNotAllowWeightsGreaterThan100() {
    assertThrows(APIException.class, () -> new Item(1, 100.001, 1));
  }

  @Test
  public void shouldAllowZeroCost() {
    new Item(1, 1F, 0);
  }

  @Test
  public void shouldNotAllowNegativeCost() {
    assertThrows(APIException.class, () -> new Item(1, 1, -0.001));
  }

  @Test
  public void shouldNotAllowCostsGreaterThan100() {
    assertThrows(APIException.class, () -> new Item(1, 1, 100.001));
  }
}
