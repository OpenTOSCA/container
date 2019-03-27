package org.opentosca.container.legacy.core.engine.resolver;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.opentosca.container.legacy.core.model.CSARContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class PathResolver {

  private static final Logger LOG = LoggerFactory.getLogger(PathResolver.class);


  /**
   * Returns the absolute path of a absolute location path and a assumed relative path. If the
   * relative path is absolute, the relative path is returned. If there is an error, null is returned.
   * <p>
   * TODO to avoid legacy problems this method handles absolute pathes in the parameter relativePath.
   * This is not specification compliant!
   *
   * @param locationPath
   * @param relativePath
   * @return the absolute path or in case of an error null
   */
  public static String resolveRelativePath(String locationPath, String relativePath, final CSARContent csarContent) {
    // String fs = System.getProperty("file.separator");
    final String fs = "/";

    locationPath = locationPath.replace("/", fs);
    locationPath = locationPath.replace("\\", fs);
    locationPath = locationPath.replace("//", fs);
    locationPath = locationPath.replace("\\\\", fs);

    relativePath = relativePath.replace("/", fs);
    relativePath = relativePath.replace("\\", fs);
    relativePath = relativePath.replace("//", fs);
    relativePath = relativePath.replace("\\\\", fs);

    LOG.trace("Resolve the relative path with " + System.getProperty("line.separator")
      + "entry point:   " + locationPath + System.getProperty("line.separator") + "relative path: "
      + relativePath);

    String newPath = locationPath.substring(0, locationPath.lastIndexOf(fs));

    // somewhere else
    if (relativePath.startsWith(".." + fs)) {
      LOG.trace("somewhere else");
      while (relativePath.startsWith(".." + fs)) {
        LOG.trace(newPath + " " + relativePath);
        if (newPath.contains(fs)) {
          newPath = newPath.substring(0, locationPath.lastIndexOf(fs));
        } else {
          newPath = "";
        }
        relativePath = relativePath.substring(3);
        LOG.trace(newPath + " " + relativePath);
      }
      if (newPath.equals("")) {
        newPath = relativePath;
      } else {
        newPath = newPath + fs + relativePath;
      }
    } else if (relativePath.startsWith("." + fs)) {
      // subdirectory
      LOG.trace("subdirectory 1 with " + newPath + " " + relativePath);
      if (!newPath.endsWith(fs)) {
        newPath = newPath + fs;
      }
      newPath = newPath + relativePath.substring(2);
    } else if (relativePath.startsWith(fs)) {
      // absolute
      LOG.trace("absolute");
      newPath = relativePath.substring(1);
    } else {
    // subdirectory again
      LOG.trace("subdirectory 2");
      if (!newPath.endsWith(fs)) {
        newPath = newPath + fs;
      }
      newPath = newPath + relativePath;

      if (null == csarContent.getFile(newPath)) {
        newPath = relativePath;
      }
    }

    try {
      if (!newPath.equals("") && null != csarContent.getFile(URLDecoder.decode(newPath, "UTF-8"))) {
        LOG.trace("New path \"" + newPath + "\" seems legit, file exists.");
        return newPath;
      }
    } catch (final UnsupportedEncodingException e) {
      LOG.error("Somebody couldn't type a method parameter", e);
    }
    LOG.error("The file at the new path \"" + newPath + "\" does not exist.");
    return null;
  }
}
