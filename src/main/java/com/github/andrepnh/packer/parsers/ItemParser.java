package com.github.andrepnh.packer.parsers;

import static com.github.andrepnh.packer.APIPreconditions.check;

import com.github.andrepnh.packer.core.Item;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ItemParser implements Parser<Iterable<Item>> {
  @Override
  public Iterable<Item> apply(String raw) {
    // To support whitespaces inside the item we split at the closing parenthesis, but to facilitate
    // validations we don't actually remove that parenthesis from the resulting string.
    // This is achieved by using positive look behind, so the regex below splits at every
    // zero-length String that is preceded by a closing parenthesis. For further details see:
    // https://stackoverflow.com/questions/4416425/how-to-split-string-with-some-separator-but-without-removing-that-separator-in-j
    List<Item> items = Stream.of(raw.split("(?<=\\))"))
        .map(String::trim)
        .filter(Predicate.not(String::isBlank))
        .map(this::toItem)
        .collect(Collectors.toList());
    check(!items.isEmpty(), "No valid items found on %s", raw);

    return items;
  }

  private Item toItem(String raw) {
    check(raw.startsWith("(") && raw.endsWith(")"), "Invalid item : %s", raw);
    var itemWithoutTrailingParenthesis = raw.substring(1, raw.length() - 1);
    var fields = Stream.of(itemWithoutTrailingParenthesis.split(","))
        .map(String::trim)
        .collect(Collectors.toList());
    check(fields.size() == 3, "Item doesn't have 3 fields: %s", raw);
    return new Item(
        tryParse("index", fields.get(0), Integer::parseInt),
        tryParse("weight", fields.get(1), Double::parseDouble),
        tryParse("cost", fields.get(2), this::parseCurrency));
  }

  private double parseCurrency(String raw) {
    check(raw.startsWith("€"), "Cost missing currency symbol: %s", raw);
    return Double.parseDouble(raw.replaceFirst("€", ""));
  }
}
