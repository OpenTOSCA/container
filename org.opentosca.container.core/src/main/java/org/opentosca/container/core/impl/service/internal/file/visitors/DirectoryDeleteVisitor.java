package org.opentosca.container.core.impl.service.internal.file.visitors;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Visits and deletes all files and directories in a directory.
 */
public class DirectoryDeleteVisitor extends SimpleFileVisitor<Path> {

  /**
   * Called for a file. It deletes the file.
   */
  @Override
  public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
    Files.delete(file);
    return super.visitFile(file, attrs);
  }

  /**
   * Called for a directory after all files and directories in the directory have been visited. It
   * deletes the directory.
   */
  @Override
  public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {
    Files.delete(dir);
    return super.postVisitDirectory(dir, exc);
  }

}
