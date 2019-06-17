package com.github.andrepnh.packer.algorithm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.andrepnh.packer.core.Input;
import com.github.andrepnh.packer.core.Item;
import com.github.andrepnh.packer.core.Package;
import com.google.common.collect.Lists;
import java.math.BigDecimal;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class MeetInTheMiddlePackingTest extends BasePackingAlgorithmTest {
  MeetInTheMiddlePackingTest() {
    super(new MeetInTheMiddlePacking());
  }

  /**
   * Testing this since it consistently failed when using this algorithm.
   */
  @Test
  void shouldFindBestPackingOptionForTheFirstTestCaseOfTheSampleFile() {
    var testCase = new Input(new BigDecimal(81), Lists.newArrayList(
        new Item(1, new BigDecimal("53.38"), new BigDecimal(45)),
        new Item(2, new BigDecimal("88.62"), new BigDecimal(98)),
        new Item(3, new BigDecimal("78.48"), new BigDecimal(3)),
        new Item(4, new BigDecimal("72.30"), new BigDecimal(76)),
        new Item(5, new BigDecimal("30.18"), new BigDecimal(9)),
        new Item(6, new BigDecimal("46.34"), new BigDecimal(48))
    ));
    var expectedItems = Lists.newArrayList(4);

    Package pack = algorithm.pack(testCase.getWeightLimit(), testCase.getItems());

    assertEquals(
        expectedItems,
        pack.getItems()
            .stream()
            .map(Item::getIndex)
            .sorted()
            .collect(Collectors.toList()));
  }

  /**
   * Testing this since it consistently failed when using this algorithm.
   */
  @Test
  void shouldFindBestPackingOptionForTheLastTestCaseOfTheSampleFile() {
    var testCase = new Input(new BigDecimal(56), Lists.newArrayList(
        new Item(1, new BigDecimal("90.72"), new BigDecimal(13)),
        new Item(2, new BigDecimal("33.80"), new BigDecimal(40)),
        new Item(3, new BigDecimal("43.15"), new BigDecimal(10)),
        new Item(4, new BigDecimal("37.97"), new BigDecimal(16)),
        new Item(5, new BigDecimal("46.81"), new BigDecimal(36)),
        new Item(6, new BigDecimal("48.77"), new BigDecimal(79)),
        new Item(7, new BigDecimal("81.80"), new BigDecimal(45)),
        new Item(8, new BigDecimal("19.36"), new BigDecimal(79)),
        new Item(9, new BigDecimal("6.76"), new BigDecimal(64))
    ));
    var expectedItems = Lists.newArrayList(8, 9);

    Package pack = algorithm.pack(testCase.getWeightLimit(), testCase.getItems());

    assertEquals(
        expectedItems,
        pack.getItems()
            .stream()
            .map(Item::getIndex)
            .sorted()
            .collect(Collectors.toList()));
  }
}
