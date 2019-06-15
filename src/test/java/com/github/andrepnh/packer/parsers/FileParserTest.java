package com.github.andrepnh.packer.parsers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.andrepnh.packer.core.Input;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
  void shouldIgnoreEmptyOptionals() throws IOException, URISyntaxException {
    Function<String, Optional<Input>> emptyOptionalLineParser = s -> Optional.empty();
    var parser = new FileParser(emptyOptionalLineParser);
    var path = Paths.get(ClassLoader.getSystemResource("abc_file.txt").toURI());

    List<Input> results = parser.parse(path)
        .collect(Collectors.toList());

    assertEquals(Collections.emptyList(), results);
  }
}
