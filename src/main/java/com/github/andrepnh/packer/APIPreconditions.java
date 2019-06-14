package com.github.andrepnh.packer;

import com.github.andrepnh.exception.APIException;

public final class APIPreconditions {
  private APIPreconditions() { }

  public static void check(boolean condition, String format, Object... parameters)
      throws APIException {
    if (!condition) {
      throw new APIException(String.format(format, parameters));
    }
  }
}
