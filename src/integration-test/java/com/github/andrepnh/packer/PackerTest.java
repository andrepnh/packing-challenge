package com.github.andrepnh.packer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class PackerTest {
  @Test
  void shouldFindBestPackingOptionFromAFile() {
    Path inputFile = pathToResource("default-test-case.txt"),
        expectedOutputFile = pathToResource("default-test-case-expected-output.txt");
    List<String> expectedOutput = trimmedLines(toList(expectedOutputFile));

    List<String> output = trimmedLines(pack(inputFile));

    assertEquals(expectedOutput, output);
  }

  private List<String> pack(Path inputFile) {
    var baos = new ByteArrayOutputStream();
    try (PrintStream outputStream = new PrintStream(baos, true, StandardCharsets.UTF_8)) {
      Packer.pack(inputFile, outputStream);
    }
    String data = new String(baos.toByteArray(), StandardCharsets.UTF_8);

    return Stream.of(data.split(System.lineSeparator()))
        .collect(Collectors.toList());
  }

  private List<String> toList(Path file) {
    try (Stream<String> lines = Files.lines(file)) {
      return lines.collect(Collectors.toList());
    } catch (IOException ex) {
      throw new IllegalStateException(ex);
    }
  }

  private List<String> trimmedLines(List<String> lines) {
    return lines.stream()
        .map(String::trim)
        .filter(Predicate.not(String::isBlank))
        .collect(Collectors.toList());
  }

  private Path pathToResource(String resource) {
    try {
      return Paths.get(ClassLoader.getSystemResource(resource).toURI());
    } catch (URISyntaxException e) {
      throw new IllegalStateException(e);
    }
  }
}
