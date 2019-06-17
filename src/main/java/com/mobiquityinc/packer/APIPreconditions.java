package com.mobiquityinc.packer;

import com.mobiquityinc.exception.APIException;

public final class APIPreconditions {
  private APIPreconditions() { }

  public static void check(boolean condition, String format, Object... parameters)
      throws APIException {
    if (!condition) {
      throw new APIException(String.format(format, parameters));
    }
  }
}
