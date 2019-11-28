package org.opentosca.container.core.service;

import java.io.File;
import java.util.List;

/**
 * Interface that provides methods to access files stored in this File Access Service.
 */
@Deprecated
public interface IFileAccessService {

  /**
   * @return file object of the TOSCA schema v.1.0-cs02
   */
  public File getOpenToscaSchemaFile();

  /**
   * @return a created Temp directory for storing files temporarily
   */
  public File getTemp();

  /**
   * Unpacks <code>zipFile</code> to a created Temp directory.
   *
   * @param zipFile to unpack
   * @return the created Temp directory containing the unpacked files
   */
  public File unpackToTemp(File zipFile);

  /**
   * Creates a new ZIP archive containing the contents of supplied <code>directory</code>.<br />
   * Existing archives with the same name will be overwritten.
   *
   * @param directory - absolute path to the directory that content (including sub directories) should
   *                  be zipped
   * @param archive   - absolute path to the ZIP archive that should be created
   */
  public File zip(File directory, File archive);

  /**
   * Unpacks the ZIP archive <code>archive</code> to <code>target</code>.
   *
   * @param archive - absolute path to the ZIP archive
   * @param target  - directory where the content of the archive should be unpacked
   * @return a list of files that were unpacked
   */
  public List<File> unzip(File archive, File target);

}
