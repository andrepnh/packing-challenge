package com.mobiquityinc.packer.parsers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.mobiquityinc.exception.APIException;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

class ParserTest {
  private final Parser<String> parser = s -> s;
  private final Function<String, Void> throwsIllegalState = s -> {
    throw new IllegalStateException();
  };

  @Test
  void shouldParseValidValues() {
    var expected = "hip";
    var result = parser.tryParse("whatever", expected, Function.identity());
    assertEquals(expected, result);
  }

  @Test
  void exceptionShouldMentionTheDescription() {
    var description = "Zzzyxas";
    try {
      parser.tryParse(description, "", throwsIllegalState);
    } catch (APIException ex) {
      assertTrue(ex.getMessage().contains(description));
    }
  }

  @Test
  void exceptionShouldMentionTheValue() {
    var value = "cheese";
    try {
      parser.tryParse("whatever", value, throwsIllegalState);
    } catch (APIException ex) {
      assertTrue(ex.getMessage().contains(value));
    }
  }

  @Test
  void shouldThrowAPIExceptionWithOriginalOneOnItsCause() {
    try {
      parser.tryParse("whatever", "foo", throwsIllegalState);
    } catch (APIException ex) {
      assertThat(ex.getCause(), instanceOf(IllegalStateException.class));
    }
  }
}
