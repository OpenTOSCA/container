package org.opentosca.container.core.impl.service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;

import org.opentosca.container.core.common.file.ResourceAccess;
import org.opentosca.container.core.service.IFileAccessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Store for files that are needed by the Container and its tests. Every file can be accessed by a
 * separate method.
 */
@Service
public class FileAccessServiceImpl implements IFileAccessService {

  private static final Logger LOG = LoggerFactory.getLogger(FileAccessServiceImpl.class);

  /**
   * @param relFilePath
   * @return the file at the file path <code>relFilePath</code> (relative to <code>META-INF/res</code>
   * in this bundle)
   */
  private File getResource(final String relFilePath) {

    URL bundleResURL = null;
    Path nioPath = null;
    File fileRes = null;

    try {
      bundleResURL = this.getClass().getResource(relFilePath);
      // convert bundle resource URL to file URL
      nioPath = ResourceAccess.resolveUrl(bundleResURL);
      // FIXME can not return nioPath.toFile, because the URI most likely points
      //  to something inside a JAR file
    } catch (final Exception e) {
      LOG.error("", e);
    }

    if (fileRes == null) {
      LOG.error("Can't get file at relative path {}.", relFilePath);
    } else {
      LOG.debug("Absolute File path: {}", fileRes.getAbsolutePath());
    }

    return fileRes;
  }


  @Override
  /**
   * {@inheritDoc}
   */
  public File getOpenToscaSchemaFile() {
    LOG.debug("Get the TOSCA XML schema");
    return this.getResource("/TOSCA-v1.0.xsd");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public File getTemp() {
    try {
      return FileSystem.getTemporaryFolder().toFile();
    } catch (final IOException exc) {
      LOG.warn("An IO Exception occured.", exc);
      return null;
    }
  }

  @Override
  /**
   * {@inheritDoc}
   */
  public File unpackToTemp(final File zipFile) {
    final File tempDir = this.getTemp();
    ZipManager.getInstance().unzip(zipFile, tempDir);
    return tempDir;
  }

  @Override
  /**
   * {@inheritDoc}
   */
  public File zip(final File directory, final File archive) {
    return ZipManager.getInstance().zip(directory, archive);
  }

  @Override
  /**
   * {@inheritDoc}
   */
  public List<File> unzip(final File file, final File toTarget) {
    return ZipManager.getInstance().unzip(file, toTarget);
  }

}
