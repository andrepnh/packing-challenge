package com.mobiquityinc.packer.parsers;

import static java.util.Objects.requireNonNull;

import com.mobiquityinc.packer.core.Input;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public class FileParser {
  private final Function<String, Optional<Input>> lineParser;

  public FileParser(Function<String, Optional<Input>> lineParser) {
    this.lineParser = requireNonNull(lineParser);
  }

  /**
   * Parses the parameterized file into a stream of {@link Input}.
   * IMPORTANT: the returned stream has to be closed in order to properly release file handles.
   */
  public Stream<Input> parse(Path file) throws IOException {
    return Files.lines(file, StandardCharsets.UTF_8)
        .map(lineParser)
        .filter(Optional::isPresent)
        .map(Optional::get);
  }
}
