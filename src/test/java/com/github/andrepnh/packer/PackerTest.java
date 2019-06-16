package com.github.andrepnh.packer;

import static java.math.BigDecimal.ONE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.andrepnh.exception.APIException;
import com.github.andrepnh.packer.algorithm.BruteForcePacking;
import com.github.andrepnh.packer.algorithm.PackingAlgorithm;
import com.github.andrepnh.packer.core.Input;
import com.github.andrepnh.packer.core.Item;
import com.github.andrepnh.packer.parsers.FileParser;
import com.github.andrepnh.packer.test.Resources;
import com.google.common.collect.Lists;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PackerTest {
  // We need a valid file to pass validations
  private Path placeholderFile;

  private Packer packer;

  private PackingAlgorithm packingAlgorithm;

  @Mock
  private FileParser fileParserMock;

  @BeforeEach
  void setup() {
    placeholderFile = Resources.asPath("abc_file.txt");
    packingAlgorithm = new BruteForcePacking();
    packer = new Packer(fileParserMock, packingAlgorithm);
  }

  @Test
  void fileParserIsMandatory() {
    assertThrows(NullPointerException.class, () -> new Packer(null, packingAlgorithm));
  }

  @Test
  void algorithmIsMandatory() {
    assertThrows(NullPointerException.class, () -> new Packer(fileParserMock, null));
  }

  @Test
  void shouldSortIndexesBeforePrinting() throws IOException {
    var items = Lists.newArrayList(
        new Item(2, ONE, ONE),
        new Item(1, ONE, ONE),
        new Item(11, ONE, ONE), // To make sure we're not sorting lexicographically
        new Item(5, ONE, ONE),
        new Item(3, ONE, ONE)
    );
    var fileContents = Stream.of(new Input(new BigDecimal(50), items));
    when(fileParserMock.parse(placeholderFile)).thenReturn(fileContents);
    String sortedIndexes = items.stream()
        .map(Item::getIndex)
        .sorted()
        .map(String::valueOf)
        .collect(Collectors.joining(Packer.INDEX_SEPARATOR));

    List<String> packageItems = pack();

    assertEquals(Collections.singletonList(sortedIndexes), packageItems);
  }

  @Test
  void shouldPrintASpecialSymbolIfPackageIsEmpty() throws IOException {
    var fileContents = Stream.of(new Input(new BigDecimal(50), Collections.emptyList()));
    when(fileParserMock.parse(placeholderFile)).thenReturn(fileContents);

    List<String> packageItems = pack();
    assertEquals(Collections.singletonList(Packer.EMPTY_PACKAGE), packageItems);
  }

  @Test
  void shouldThrowExceptionIfPathIsInvalid() {
    assertThrows(APIException.class, () -> packer.packAndPrint(Path.of("whatever"), System.out));
  }

  @Test
  void shouldThrowExceptionIfFilePathIsNullOrEmpty() {
    for (String filePath: new String[] {null, ""}) {
      assertThrows(APIException.class, () -> Packer.pack(filePath));
    }
  }

  @Test
  void shouldCloseParserStream() throws IOException {
    // Quick workaround for "final" limitation on lambda expressions
    var closed = new boolean[] {false};
    // Mockito can't mock streams, so we have to use onClose to verify resource cleanup
    var parserStream = Stream.<Input>empty().onClose(() -> closed[0] = true);
    when(fileParserMock.parse(placeholderFile)).thenReturn(parserStream);

    packer.packAndPrint(placeholderFile, System.out);

    assertTrue(closed[0], "File parsers stream was not properly closed");
  }

  @Test
  void shouldThrowExceptionIfAnIoErrorHappens() throws IOException {
    when(fileParserMock.parse(placeholderFile)).thenThrow(new IOException());

    assertThrows(APIException.class, () -> packer.packAndPrint(placeholderFile, System.out));
    verify(fileParserMock).parse(placeholderFile);
  }

  private List<String> pack() {
    var baos = new ByteArrayOutputStream();
    try (PrintStream outputStream = new PrintStream(baos, true, StandardCharsets.UTF_8)) {
      packer.packAndPrint(placeholderFile, outputStream);
    }
    String data = new String(baos.toByteArray(), StandardCharsets.UTF_8);

    return Stream.of(data.split(System.lineSeparator()))
        .collect(Collectors.toList());
  }
}
