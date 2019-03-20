package org.opentosca.container.integration.tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public final class TestingUtil {

  private TestingUtil() {
    throw new UnsupportedOperationException();
  }

  public static File pathToFile(final String relativePath) {
    final URL url = pathToURL(relativePath);
    File file;
    try {
      file = new File(url.toURI());
    } catch (URISyntaxException e) {
      file = new File(url.getPath());
    }
    return file;
  }

  public static URL pathToURL(final String relativePath) {
    final Bundle bundle = FrameworkUtil.getBundle(TestingUtil.class);
    return bundle.getResource(relativePath);
  }

  public static String getExternalIpAddress() {
    try {
      final URL url = new URL("http://checkip.amazonaws.com");
      final BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
      return in.readLine();
    } catch (Exception e) {
      return "127.0.0.1";
    }
  }
}
