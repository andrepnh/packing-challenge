package com.mobiquityinc.packer.core;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.mobiquityinc.exception.APIException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ItemTest {
  @ParameterizedTest
  @ValueSource(ints = { 0, -1, Integer.MIN_VALUE })
  void shouldNotAllowZeroOrNegativeIndex(int badIndex) {
      assertThrows(APIException.class, () -> new Item(badIndex, ONE, ONE));
  }

  @Test
  void shouldAllowZeroWeight() {
    new Item(1, ZERO, ONE);
  }

  @Test
  void shouldNotAllowNegativeWeight() {
    assertThrows(APIException.class, () -> new Item(1, new BigDecimal("-0.001"), ONE));
  }

  @Test
  void shouldNotAllowWeightsGreaterThan100() {
    assertThrows(APIException.class, () -> new Item(1, new BigDecimal("100.001"), ONE));
  }

  @Test
  void shouldAllowZeroCost() {
    new Item(1, ONE, ZERO);
  }

  @Test
  void shouldNotAllowNegativeCost() {
    assertThrows(APIException.class, () -> new Item(1, ONE, new BigDecimal("-0.001")));
  }

  @Test
  void shouldNotAllowCostsGreaterThan100() {
    assertThrows(APIException.class, () -> new Item(1, ONE, new BigDecimal("100.001")));
  }
}
