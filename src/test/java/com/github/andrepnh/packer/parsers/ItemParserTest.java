package com.github.andrepnh.packer.parsers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.andrepnh.exception.APIException;
import com.github.andrepnh.packer.core.Item;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ItemParserTest {
  private final ItemParser parser = new ItemParser();

  @Test
  void shouldNotAllowEmptyItems() {
    assertThrows(APIException.class, () -> parser.apply(""));
  }

  @Test
  void shouldParseMultipleItems() {
    var rawItems = "(1,2.2,€3.3) (55,66.66,€77.77)";
    var expectedItems = Lists.newArrayList(
        new Item(1, 3.3, 2.2),
        new Item(55, 77.77, 66.66)
    );

    Iterable<Item> items = parser.apply(rawItems);

    assertEquals(expectedItems, Lists.newArrayList(items));
  }

  @ParameterizedTest
  @ValueSource(strings = {": (1,2.2,€3.3)", "(1,2.2,€3.3) ("})
  void shouldNotParseExtraSymbolsOutsideItem(String invalidItem) {
    assertThrows(APIException.class, () -> parser.apply(invalidItem));
  }

  @ParameterizedTest
  @ValueSource(strings = {"()", "(1)", "(1,2.5)"})
  void shouldNotAllowItemWithLessThan3Fields(String invalidItem) {
    assertThrows(APIException.class, () -> parser.apply(invalidItem));
  }

  @ParameterizedTest
  @ValueSource(strings = {"(,2.2,€3.3)", "(1,,€3.3)", "(1,2.2,)"})
  void shouldNotAllowEmptyFields(String invalidItem) {
    assertThrows(APIException.class, () -> parser.apply(invalidItem));
  }

  @Test
  void shouldNotAllowItemWithMoreThan3Fields() {
    assertThrows(APIException.class, () -> parser.apply("(1,2.2,€3.3,4)"));
  }

  @ParameterizedTest
  @ValueSource(strings = {"1,2.2,€3.3)", "(1,2.2,€3.3", "((1,2.2,€3.3)", "(1,2.2,€3.3))"})
  void shouldNotParseItemWithUnbalancedParenthesis(String invalidItem) {
    assertThrows(APIException.class, () -> parser.apply(invalidItem));
  }

  @Test
  void shouldNotParseItemWithExtraParenthesis() {
    assertThrows(APIException.class, () -> parser.apply("((1,2.2,€3.3))"));
  }

  @ParameterizedTest
  @ValueSource(strings = {"1,2.2,€3.3", "[1,2.2,€3.3]", "{1,2.2,€3.3}"})
  void shouldNotParseItemMissingParenthesis(String invalidItem) {
    assertThrows(APIException.class, () -> parser.apply(invalidItem));
  }

  @Test
  void shouldIgnoreTrailingWhitespaceAroundParenthesis() {
    var rawItem = "  (1,2.2,€3.3) ";
    var expectedItem = new Item(1, 3.3, 2.2);

    Iterable<Item> items = parser.apply(rawItem);

    assertEquals(Lists.newArrayList(expectedItem), Lists.newArrayList(items));
  }

  @Test
  void shouldIgnoreWhitespaceBetweenSeparators() {
    var rawItem = "(1 , 2.2  ,   €3.3)";
    var expectedItem = new Item(1, 3.3, 2.2);

    Iterable<Item> items = parser.apply(rawItem);

    assertEquals(Lists.newArrayList(expectedItem), Lists.newArrayList(items));
  }

  @Test
  void shouldNotAllowNonNumericIndex() {
    assertThrows(APIException.class, () -> parser.apply("(1a,2.2,€3.3)"));
  }

  @Test
  void shouldNotAllowNonNumericWeight() {
    assertThrows(APIException.class, () -> parser.apply("(1,2.b,€3.3)"));
  }

  @Test
  void shouldNotAllowNonNumericCost() {
    assertThrows(APIException.class, () -> parser.apply("(1,2.2,€a3.3)"));
  }

  @Test
  void shouldNotAllowCostMissingCurrencySymbol() {
    assertThrows(APIException.class, () -> parser.apply("(1,2.2,3.3)"));
  }

  @Test
  void shouldNotAllowCostWithMultipleCurrencySymbols() {
    assertThrows(APIException.class, () -> parser.apply("(1,2.2,€€3.3)"));
  }

  @Test
  void shouldNotAllowFloatingPointIndex() {
    assertThrows(APIException.class, () -> parser.apply("(1.0,2.2,€3.3)"));
  }

  @Test
  void shouldAllowIntegerWeight() {
    var rawItem = "(1,2,€3.3)";
    var expectedItem = new Item(1, 3.3, 2);

    Iterable<Item> items = parser.apply(rawItem);

    assertEquals(Lists.newArrayList(expectedItem), Lists.newArrayList(items));
  }

  @Test
  void shouldAllowIntegerCost() {
    var rawItem = "(1,2.2,€3)";
    var expectedItem = new Item(1, 3, 2.2);

    Iterable<Item> items = parser.apply(rawItem);

    assertEquals(Lists.newArrayList(expectedItem), Lists.newArrayList(items));
  }
}
