package com.github.andrepnh.packer.parsers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.andrepnh.exception.APIException;
import com.github.andrepnh.packer.core.Input;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class LineParserTest {
  private final LineParser parser = new LineParser(s -> Collections.emptyList());

  @Test
  void itemParserShouldBeMandatory() {
    assertThrows(NullPointerException.class, () -> new LineParser(null));
  }

  @Test
  void shouldParseValidLine() {
    var weightLimit = 11;
    Optional<Input> result = parser.apply(weightLimit + " : whatever");
    assertEquals(weightLimit, result.map(Input::getWeightLimit).orElse(null));
  }

  @Test
  void shouldIgnoreTrailingWhitespace() {
    var weightLimit = 51;
    var line = String.format(" \t%d : whatever\n", weightLimit);
    Optional<Input> result = parser.apply(line);
    assertEquals(weightLimit, result.map(Input::getWeightLimit).orElse(null));
  }

  @Test
  void shouldReturnEmptyOptionalOnEmptyLine() {
    assertEquals(Optional.empty(), parser.apply(" "));
  }

  @Test
  void shouldParseLineEvenIfThereIsNoWhitespaceAroundTheWeightLimitSeparator() {
    var weightLimit = 99;
    Optional<Input> result = parser.apply(weightLimit + ":whatever");
    assertEquals(weightLimit, result.map(Input::getWeightLimit).orElse(null));
  }

  @Test
  void shouldThrowExceptionIfTheWeightLimitSeparatorIsMissing() {
    assertThrows(APIException.class, () -> parser.apply("51 (1,2.2,€3.3)"));
  }

  @Test
  void shouldThrowExceptionOnMultipleWeightLimitSeparators() {
    assertThrows(APIException.class, () -> parser.apply("51 :: whatever"));
  }

  @Test
  void shouldThrowExceptionIfWeightLimitIsNotANumber() {
    assertThrows(APIException.class, () -> parser.apply("2b : whatever"));
  }

  @Test
  void shouldThrowExceptionIfWeightLimitIsMissing() {
    assertThrows(APIException.class, () -> parser.apply(" : whatever"));
  }
}
