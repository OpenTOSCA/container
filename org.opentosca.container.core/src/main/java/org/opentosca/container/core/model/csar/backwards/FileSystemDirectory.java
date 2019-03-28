package org.opentosca.container.core.model.csar.backwards;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.opentosca.container.core.model.AbstractDirectory;
import org.opentosca.container.core.model.AbstractFile;

// we want to remove the idea of directories
@Deprecated
public class FileSystemDirectory extends AbstractDirectory {

  private final Path representedPath;

  public FileSystemDirectory(Path dirPath) {
    super(dirPath.toAbsolutePath().toString(), Collections.emptySet(), Collections.emptySet(), false);
    if (Files.exists(dirPath) && !Files.isDirectory(dirPath)) {
      throw new IllegalArgumentException("Given path " + dirPath + " was not a directory");
    }
    representedPath = dirPath;
  }

  @Override
  public AbstractFile getFile(String relPathOfFile) {
    Path resolved = representedPath.resolve(relPathOfFile);
    if (Files.exists(resolved) && Files.isRegularFile(resolved)) {
      return new FileSystemFile(resolved);
    }
    return null;
  }

  @Override
  public Set<AbstractFile> getFilesRecursively() {
    try {
      return Files.walk(representedPath)
        .filter(Files::isRegularFile)
        .map(FileSystemFile::new)
        .collect(Collectors.toSet());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public String getPath() {
    return representedPath.toAbsolutePath().toString();
  }

  @Override
  public Set<AbstractDirectory> getDirectories() {
    Set<Path> result = new HashSet<>();
    try (DirectoryStream<Path> stream = Files.newDirectoryStream(representedPath)) {
      for (Path entry : stream) {
        if (Files.isDirectory(entry)) {
          result.add(entry);
        }
      }
    } catch (DirectoryIteratorException ex) {
      // I/O error encounted during the iteration, the cause is an IOException
      throw new UncheckedIOException((IOException) ex.getCause());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    return result.stream().map(FileSystemDirectory::new).collect(Collectors.toSet());
  }

  @Override
  protected AbstractFile getFileNotConsiderPatterns(String relPathOfFile) {
    return new FileSystemFile(representedPath.resolve(relPathOfFile));
  }

  @Override
  protected Set<AbstractFile> getFilesNotConsiderPatterns() {
    Set<Path> result = new HashSet<>();
    try (DirectoryStream<Path> stream = Files.newDirectoryStream(representedPath)) {
      for (Path entry : stream) {
        if (Files.isRegularFile(entry)) {
          result.add(entry);
        }
      }
    } catch (DirectoryIteratorException ex) {
      // I/O error encounted during the iteration, the cause is an IOException
      throw new UncheckedIOException((IOException) ex.getCause());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    return result.stream().map(FileSystemFile::new).collect(Collectors.toSet());
  }

  @Override
  public AbstractDirectory getDirectory(String relPathOfDirectory) {
    Path resolved = representedPath.resolve(relPathOfDirectory);
    if (Files.exists(resolved) && Files.isDirectory(resolved)) {
      return new FileSystemDirectory(resolved);
    }
    return null;
  }

  @Override
  public String getName() {
    // getFileName just returns the last path fragment
    return representedPath.getFileName().toString();
  }

}
