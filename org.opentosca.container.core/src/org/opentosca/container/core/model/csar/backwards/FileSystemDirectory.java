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
        if (!Files.isDirectory(dirPath)) {
            throw new IllegalArgumentException();
        }
        representedPath = dirPath;
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
        }
        catch (DirectoryIteratorException ex) {
            // I/O error encounted during the iteration, the cause is an IOException
            throw new UncheckedIOException((IOException)ex.getCause());
        }
        catch (IOException e) {
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
        }
        catch (DirectoryIteratorException ex) {
            // I/O error encounted during the iteration, the cause is an IOException
            throw new UncheckedIOException((IOException)ex.getCause());
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return result.stream().map(FileSystemFile::new).collect(Collectors.toSet());
    }

    @Override
    public AbstractDirectory getDirectory(String relPathOfDirectory) {
        return new FileSystemDirectory(representedPath.resolve(relPathOfDirectory));
    }

    @Override
    public String getName() {
        // getFileName just returns the last path fragment
        return representedPath.getFileName().toString();
    }

}
