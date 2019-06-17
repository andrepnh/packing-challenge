package com.mobiquityinc.packer.parsers;

import com.mobiquityinc.exception.APIException;
import java.util.function.Function;

public interface Parser<T> extends Function<String, T> {

  /**
   * Convenience method to parsers and wrap any exceptions into {@link APIException}.
   *
   * @param description description of what is being parsers; will be part of the exception message
   * @param raw value to parsers
   * @param parsingFunction function that does the actual parsing
   */
  default <V> V tryParse(String description, String raw, Function<String, V> parsingFunction) {
    try {
      return parsingFunction.apply(raw);
    } catch (Exception ex) {
      throw new APIException(ex, "Invalid %s: %s", description, raw);
    }
  }
}
