package org.opentosca.container.core.impl.service;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static java.nio.file.FileVisitResult.CONTINUE;

@NonNullByDefault
public class FileSystem {

  private static final Path FS_TEMP = Paths.get(System.getProperty("java.io.tmpdir"));
  private static final Logger LOG = LoggerFactory.getLogger(FileSystem.class);

  /**
   * Creates a ready-to-use directory that previously did not exist.
   *
   * @return A {@link Path} pointing towards a folder within the system-designated temporary directory as
   *  indicated by the {@literal java.io.tmpdir} system property.
   */
  // synchronized to avoid race-conditions, TODO investigate an internal mutex?
  public static synchronized Path getTemporaryFolder() {
    Path candidate = FS_TEMP.resolve(String.valueOf(System.nanoTime()));
    while (Files.exists(candidate)) {
      candidate = FS_TEMP.resolve(String.valueOf(System.nanoTime()));
    }
    // create the directory
    while (true) {
      try {
        Files.createDirectories(candidate);
      } catch (IOException e) {
        LOG.warn("Could not create temporary directory {}", candidate, e);
        continue;
      }
      return candidate;
    }
  }

  public static Path unpackToTemp(Path zipFile) throws IOException {
    Path targetDir = getTemporaryFolder();
    unzip(zipFile, targetDir);
    return targetDir;
  }

  public static void zip(Path targetFile, Path... inputFiles) throws IOException {
    if (inputFiles.length == 0) { return; }
    // recurse into a single base-directory
    if (inputFiles.length == 1 && Files.isDirectory(inputFiles[0])) {
      final Path[] inputs;
      try (Stream<Path> search = Files.find(inputFiles[0], Integer.MAX_VALUE, (p, a) -> true)) {
        inputs = search.toArray(Path[]::new);
      } catch (IOException inner) {
        throw inner;
      }
      zip(targetFile, inputs);
    }
    // the root to relativize paths against for the name-resolution of the Zip entries
    final Path root = Files.isDirectory(inputFiles[0]) ? inputFiles[0] : inputFiles[0].getParent();
    // ensure parent file is a thing
    Files.createDirectories(targetFile.getParent());
    // ensure target file is empty and exists
    Files.deleteIfExists(targetFile);
    Files.createFile(targetFile);
    // create a filesystem for the zipfile
    java.nio.file.FileSystem zipFs = FileSystems.newFileSystem(targetFile, FileSystem.class.getClassLoader());
    try {
      for (Path externalFile : inputFiles) {
        final Path relative = root.relativize(externalFile);
        final Path zipInternal = zipFs.getPath(relative.toString());
        if (Files.isDirectory(externalFile)) {
          Files.createDirectories(zipInternal);
        } else {
          // ensure parent directory exists
          Files.createDirectories(zipInternal.getParent());
          Files.copy(externalFile, zipInternal, StandardCopyOption.REPLACE_EXISTING);
        }
      }
    } finally {
      zipFs.close();
    }
  }

  public static List<Path> unzip(Path zipFile, Path targetDirectory) throws IOException {
    // ensure target directory exists
    Files.createDirectories(targetDirectory);
    // take note of all the files stored in this zipFile
    final List<String> zipStoredNames = new ArrayList<>();
    try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipFile, StandardOpenOption.READ))) {
      ZipEntry currentEntry = zis.getNextEntry();
      while (currentEntry != null) {
        if (!currentEntry.isDirectory()) {
          zipStoredNames.add(currentEntry.getName());
        }
        currentEntry = zis.getNextEntry();
      }
    }

    // create a filesystem for the zipfile
    java.nio.file.FileSystem zipFs = FileSystems.newFileSystem(zipFile, FileSystem.class.getClassLoader());
    try {
      return zipStoredNames.stream()
        .map(zipName -> {
          // SIDE-EFFECTING MAP TO TRANSFER THE FILES
          Path targetFile = targetDirectory.resolve(zipName);
          Path zipInternalFile = zipFs.getPath(zipName);
          try {
            Files.createDirectories(targetFile.getParent());
            Files.copy(zipInternalFile, targetFile);
            return targetFile;
          } catch (IOException e) {
            LOG.warn("Failed to extract {} from zipFile {} to {}", zipInternalFile, zipFile, targetFile, e);
            return null;
          }
        })
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
    } finally {
      zipFs.close();
    }
  }

  public static void copyDirectory(Path source, Path target) throws IOException {
    Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
      @Override
      public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes basicFileAttributes) throws IOException {
        Path targetdir = target.resolve(source.relativize(dir));
        try {
          Files.copy(dir, targetdir);
        } catch (FileAlreadyExistsException e) {
          if (!Files.isDirectory(targetdir))
            throw e;
        }
        return CONTINUE;
      }

      @Override
      public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes) throws IOException {
        Files.copy(source, target.resolve(source.relativize(source)));
        return CONTINUE;
      }
    });
  }
}
