package com.github.andrepnh.packer.parsers;

import static com.github.andrepnh.packer.APIPreconditions.check;
import static java.util.Objects.requireNonNull;

import com.github.andrepnh.packer.core.Input;
import com.github.andrepnh.packer.core.Item;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LineParser implements Parser<Optional<Input>> {
  private static final String WEIGHT_LIMIT_SEPARATOR = ":";

  private final Function<String, Iterable<Item>> itemParser;

  public LineParser(Function<String, Iterable<Item>> itemParser) {
    this.itemParser = requireNonNull(itemParser);
  }

  @Override
  public Optional<Input> apply(String line) {
    if (line.isBlank()) {
      return Optional.empty();
    }
    List<String> weightLimitAndItems = Stream.of(line.split(WEIGHT_LIMIT_SEPARATOR))
        .map(String::trim)
        .collect(Collectors.toList());
    check(weightLimitAndItems.size() == 2,
        "A line should have a single %s",
        WEIGHT_LIMIT_SEPARATOR);

    return Optional.of(new Input(
        tryParse("weightLimit", weightLimitAndItems.get(0), Integer::parseInt),
        itemParser.apply(weightLimitAndItems.get(1))
    ));
  }
}
