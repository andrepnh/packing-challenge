package com.github.andrepnh.packer.test;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class Resources {
  private Resources() { }

  public static Path asPath(String classPathResource) {
    try {
      return Paths.get(ClassLoader.getSystemResource(classPathResource).toURI());
    } catch (URISyntaxException e) {
      throw new IllegalStateException(e);
    }
  }
}
