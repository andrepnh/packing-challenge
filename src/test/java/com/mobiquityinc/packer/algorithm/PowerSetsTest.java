package com.mobiquityinc.packer.algorithm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

class PowerSetsTest {
  @Test
  void shouldThrowExceptionIfSetHasMoreThan31Elements() {
    var set = IntStream.range(0, 32).boxed().collect(Collectors.toSet());
    assertThrows(IllegalArgumentException.class, () -> PowerSets
        .find(set, Sets::newHashSetWithExpectedSize, HashSet::new));
  }

  @Test
  void shouldFindPowerSetOfASet() {
    var set = IntStream.range(0, 15).boxed().collect(Collectors.toSet());
    var expectedPowerSet = Sets.powerSet(set);

    var powerSet = PowerSets.find(set, Sets::newHashSetWithExpectedSize, HashSet::new);

    assertEquals(expectedPowerSet, powerSet);
  }
}
