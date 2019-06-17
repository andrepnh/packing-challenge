package com.mobiquityinc.packer.parsers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.mobiquityinc.packer.core.Input;
import com.mobiquityinc.packer.test.Resources;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class FileParserTest {
  @Test
  void lineParserShouldBeMandatory() {
    assertThrows(NullPointerException.class, () -> new FileParser(null));
  }

  @Test
  void shouldIgnoreEmptyOptionals() throws IOException {
    Function<String, Optional<Input>> emptyOptionalLineParser = s -> Optional.empty();
    var parser = new FileParser(emptyOptionalLineParser);
    var path = Resources.asPath("abc_file.txt");

    List<Input> results = parser.parse(path)
        .collect(Collectors.toList());

    assertEquals(Collections.emptyList(), results);
  }
}
