package org.opentosca.container.legacy.core.service.csar;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.impl.service.FileSystem;
import org.opentosca.container.core.impl.service.ZipManager;
import org.opentosca.container.core.impl.service.internal.file.visitors.DirectoryDeleteVisitor;
import org.opentosca.container.core.impl.service.internal.file.visitors.DirectoryVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unpacks a CSAR file and gets its files and directories.
 */
public class CSARUnpacker {

  private final static Logger LOG = LoggerFactory.getLogger(CSARUnpacker.class);

  private final Path csarFile;

  private Path csarUnpackDirectory;
  private DirectoryVisitor csarVisitor;

  /**
   * Default constructor needed by OSGi to instantiate this class.
   */
  public CSARUnpacker() {
    this.csarFile = null;
  }

  /**
   * Creates a {@code CSARUnpacker}.
   *
   * @param csarFile to process.
   */
  public CSARUnpacker(final Path csarFile) {
    if (csarFile == null) {
      throw new IllegalArgumentException("CsarFile may not be null");
    }

    this.csarFile = csarFile;
  }

  /**
   * Unpacks the CSAR file to a temporary directory and gets all files and directories in the unpack directory.<br />
   * <br />
   * Note: If unpacking or getting files and directories in unpacking directory failed, deleting
   * unpack directory will be attempted.
   *
   * @throws SystemException if unpacking or getting files and directories in unpack directory failed.
   */
  public void unpackAndVisitUnpackDir() throws UserException, SystemException {
    LOG.debug("Unpacking CSAR located at \"{}\"...", this.csarFile);

    try {
      this.csarUnpackDirectory = FileSystem.getTemporaryFolder();
      LOG.debug("Unpacking directory: {}", csarUnpackDirectory);

      final List<File> unpackedFiles = ZipManager.getInstance().unzip(this.csarFile.toFile(), csarUnpackDirectory.toFile());
      if (unpackedFiles == null) {
        throw new UserException("Unpacking file located at \"" + this.csarFile.toString() + "\" failed.");
      }
      this.visitUnpackDir();

      LOG.debug("Unpacking CSAR located at \"{}\" and getting its files and directories completed.", this.csarFile);

    } catch (UserException | SystemException exc) {
      this.deleteUnpackDir();
      throw exc;
    }
  }

  /**
   * @return Directory where the CSAR was unpacked. If {@link #unpackAndVisitUnpackDir()} was not
   * executed yet or failed {@code null}.
   */
  public Path getUnpackDirectory() {
    return this.csarUnpackDirectory;
  }

  /**
   * @return Files and directories in CSAR unpack directory as {@link DirectoryVisitor}. If
   * {@link #unpackAndVisitUnpackDir()} was not executed yet or failed {@code null}.
   */
  public DirectoryVisitor getFilesAndDirectories() {
    return this.csarVisitor;
  }

  /**
   * Getting recursively all files and directories in the unpack directory.
   *
   * @throws SystemException if access to an directory denied that must be visited
   */
  private void visitUnpackDir() throws SystemException {
    LOG.debug("Getting files and directories in CSAR unpack directory \"{}\"...", this.csarUnpackDirectory);
    final DirectoryVisitor directoryVisitor = new DirectoryVisitor();

    try {
      Files.walkFileTree(this.csarUnpackDirectory, directoryVisitor);
      // removes the unpack directory (only the directory content is
      // necessary)
      directoryVisitor.getVisitedDirectories().remove(this.csarUnpackDirectory);

      LOG.debug("Getting files and directories in CSAR unpack directory \"{}\" completed.", this.csarUnpackDirectory);
      this.csarVisitor = directoryVisitor;

    } catch (final IOException exc) {
      throw new SystemException(
        "An IO Exception occurred. Getting files and directorties in CSAR unpack directory \""
          + this.csarUnpackDirectory.toString() + "\" failed.",
        exc);
    }

  }

  /**
   * Deletes the unpack directory.
   *
   * @throws SystemException if access to an directory denied that must be deleted.
   */
  public void deleteUnpackDir() throws SystemException {
    if (this.csarUnpackDirectory == null) {
      return;
    }
    LOG.debug("Deleting CSAR unpack dir \"{}\"...", this.csarUnpackDirectory);

    final DirectoryDeleteVisitor csarDeleteVisitor = new DirectoryDeleteVisitor();
    try {
      Files.walkFileTree(this.csarUnpackDirectory, csarDeleteVisitor);
      LOG.debug("Deleting CSAR unpack dir \"{}\" completed.", this.csarUnpackDirectory);
    } catch (final IOException exc) {
      throw new SystemException(
        "An IO Exception occurred. Deleting files and directories in CSAR unpack directory \""
          + this.csarUnpackDirectory.toString() + "\" failed.",
        exc);
    } finally {
      this.csarUnpackDirectory = null;
      this.csarVisitor = null;
    }
  }
}
