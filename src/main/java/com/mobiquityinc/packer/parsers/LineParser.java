package com.mobiquityinc.packer.parsers;

import static com.mobiquityinc.packer.APIPreconditions.check;
import static java.util.Objects.requireNonNull;

import com.mobiquityinc.packer.core.Input;
import com.mobiquityinc.packer.core.Item;
import java.math.BigDecimal;
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
        tryParse("weightLimit", weightLimitAndItems.get(0), BigDecimal::new),
        itemParser.apply(weightLimitAndItems.get(1))
    ));
  }
}
