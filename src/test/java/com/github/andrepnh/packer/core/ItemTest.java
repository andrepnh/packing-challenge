package com.github.andrepnh.packer.core;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.andrepnh.exception.APIException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ItemTest {
  @ParameterizedTest
  @ValueSource(ints = { 0, -1, Integer.MIN_VALUE })
  void shouldNotAllowZeroOrNegativeIndex(int badIndex) {
      assertThrows(APIException.class, () -> new Item(badIndex, 1, 1));
  }

  @Test
  void shouldAllowZeroWeight() {
    new Item(1, 1, 0);
  }

  @Test
  void shouldNotAllowNegativeWeight() {
    assertThrows(APIException.class, () -> new Item(1, 1, -0.001));
  }

  @Test
  void shouldNotAllowWeightsGreaterThan100() {
    assertThrows(APIException.class, () -> new Item(1, 1, 100.001));
  }

  @Test
  void shouldAllowZeroCost() {
    new Item(1, 0, 1F);
  }

  @Test
  void shouldNotAllowNegativeCost() {
    assertThrows(APIException.class, () -> new Item(1, -0.001, 1));
  }

  @Test
  void shouldNotAllowCostsGreaterThan100() {
    assertThrows(APIException.class, () -> new Item(1, 100.001, 1));
  }
}
