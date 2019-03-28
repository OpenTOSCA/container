package org.opentosca.container.core.model.csar.backwards;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.model.AbstractFile;

@Deprecated
public class FileSystemFile extends AbstractFile {

  private final Path actualPath;

  public FileSystemFile(Path actualPath) {
    super(actualPath.toString());
    if (!Files.isRegularFile(actualPath)) {
      throw new IllegalArgumentException();
    }
    this.actualPath = actualPath;
  }

  @Override
  public Path getFile() throws SystemException {
    return actualPath;
  }

  @Override
  public InputStream getFileAsInputStream() throws SystemException {
    try {
      return Files.newInputStream(actualPath, StandardOpenOption.READ);
    } catch (IOException e) {
      throw new SystemException("Could not create input stream", e);
    }
  }

  @Override
  public String getName() {
    return actualPath.getFileName().toString();
  }

  @Override
  public String toString() {
    return actualPath.toString();
  }

  @Override
  public boolean equals(Object other) {
    if (other instanceof FileSystemFile) {
      return this.actualPath.equals(((FileSystemFile) other).actualPath);
    }
    return false;
  }

}
