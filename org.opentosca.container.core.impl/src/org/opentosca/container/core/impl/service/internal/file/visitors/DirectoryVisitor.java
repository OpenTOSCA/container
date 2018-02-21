package org.opentosca.container.core.impl.service.internal.file.visitors;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;

/**
 * Visits and remembers all files and directories in a directory.
 */
public class DirectoryVisitor extends SimpleFileVisitor<Path> {

    /**
     * Visited files and directories in the directory.
     */

    private final Set<Path> VISITED_FILES = new HashSet<>();
    private final Set<Path> VISITED_DIRECTORIES = new HashSet<>();


    /**
     * Called for a file. The file path be saved in a {@code Set}.
     */
    @Override
    public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
        this.VISITED_FILES.add(file);
        return super.visitFile(file, attrs);
    }

    /**
     * Called for a directory after all files and directories in the directory have been visited. The
     * directory path be saved in a {@code Set}.
     */
    @Override
    public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {
        this.VISITED_DIRECTORIES.add(dir);
        return super.postVisitDirectory(dir, exc);
    }

    /**
     * @return Visited files.
     */
    public Set<Path> getVisitedFiles() {
        return this.VISITED_FILES;
    }

    /**
     * @return Visited directories.
     */
    public Set<Path> getVisitedDirectories() {
        return this.VISITED_DIRECTORIES;
    }

}
