package com.github.andrepnh.exception;

// If we used checked exceptions then we'd have to add "throws" pretty much everywhere since this is
// associated with core validation. Another option would be to throw another exception and map to
// this one, but in the end it's much simpler to just use an unchecked exception. It also makes
// sense, because there's no point in forcing all clients to recover from passing bad arguments.
public class APIException extends RuntimeException {
  public APIException() {
  }

  public APIException(String message) {
    super(message);
  }

  public APIException(Throwable cause, String format, Object... args) {
    super(String.format(format, args), cause);
  }

  public APIException(Throwable cause) {
    super(cause);
  }
}
