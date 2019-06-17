package com.github.andrepnh.packer;

import static com.github.andrepnh.packer.APIPreconditions.check;
import static java.util.Objects.requireNonNull;

import com.github.andrepnh.exception.APIException;
import com.github.andrepnh.packer.algorithm.BruteForcePacking;
import com.github.andrepnh.packer.algorithm.MeetInTheMiddlePacking;
import com.github.andrepnh.packer.algorithm.PackingAlgorithm;
import com.github.andrepnh.packer.core.Input;
import com.github.andrepnh.packer.core.Item;
import com.github.andrepnh.packer.core.Package;
import com.github.andrepnh.packer.parsers.FileParser;
import com.github.andrepnh.packer.parsers.ItemParser;
import com.github.andrepnh.packer.parsers.LineParser;
import com.google.common.base.Strings;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Packer {
  public static final String INDEX_SEPARATOR = ",";
  public static final String EMPTY_PACKAGE = "-";

  private final FileParser parser;
  private final PackingAlgorithm algorithm;

  public Packer(FileParser parser, PackingAlgorithm algorithm) {
    this.parser = requireNonNull(parser);
    this.algorithm = requireNonNull(algorithm);
  }

  public static void pack(String filePath) throws APIException {
    check(!Strings.isNullOrEmpty(filePath), "Missing file");
    try {
      pack(Path.of(filePath), System.out);
    } catch (InvalidPathException ex) {
      throw new APIException(ex, "Invalid path: %s", filePath);
    }
  }

  public static void pack(Path inputFile, PrintStream outputStream) throws APIException {
    var packer = new Packer(
        new FileParser(new LineParser(new ItemParser())),
        new MeetInTheMiddlePacking()
    );
    packer.packAndPrint(inputFile, outputStream);
  }

  public void packAndPrint(Path inputFile, PrintStream outputStream) {
    check(Files.exists(requireNonNull(inputFile)), "%s is not a valid file", inputFile);
    try (Stream<Input> cases = parser.parse(inputFile)) {
      cases.map(c -> algorithm.pack(c.getWeightLimit(), c.getItems()))
          .forEach(p -> print(p, outputStream));
    } catch (IOException ex) {
      throw new APIException(ex, "Could not read from file %s", inputFile);
    }
  }

  private void print(Package packge, PrintStream printStream) {
    requireNonNull(printStream);
    String itemIndexes = packge.getItems().stream()
        .map(Item::getIndex)
        .sorted() // Sort by integer instead of strings so we don't end up with lexicographic order
        .map(String::valueOf)
        .collect(Collectors.joining(INDEX_SEPARATOR));
    printStream.println(itemIndexes.isBlank() ? EMPTY_PACKAGE : itemIndexes);
  }
}
