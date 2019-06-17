package com.mobiquityinc.packer;

import static com.mobiquityinc.packer.APIPreconditions.check;
import static java.util.Objects.requireNonNull;

import com.mobiquityinc.exception.APIException;
import com.mobiquityinc.packer.algorithm.MeetInTheMiddlePacking;
import com.mobiquityinc.packer.algorithm.PackingAlgorithm;
import com.mobiquityinc.packer.core.Input;
import com.mobiquityinc.packer.core.Item;
import com.mobiquityinc.packer.core.Package;
import com.mobiquityinc.packer.parsers.FileParser;
import com.mobiquityinc.packer.parsers.ItemParser;
import com.mobiquityinc.packer.parsers.LineParser;
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
