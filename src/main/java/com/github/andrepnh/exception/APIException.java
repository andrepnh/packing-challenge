package com.github.andrepnh.exception;

public class APIException extends Exception {
  public APIException() {
  }

  public APIException(String message) {
    super(message);
  }

  public APIException(String message, Throwable cause) {
    super(message, cause);
  }

  public APIException(Throwable cause) {
    super(cause);
  }
}
