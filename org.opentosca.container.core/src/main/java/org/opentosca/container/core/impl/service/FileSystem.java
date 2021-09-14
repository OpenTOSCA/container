package org.opentosca.container.core.impl.service;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NonNullByDefault
public class FileSystem {

    private static final Path FS_TEMP = Paths.get(System.getProperty("java.io.tmpdir"));
    private static final Logger LOG = LoggerFactory.getLogger(FileSystem.class);

    /**
     * Creates a ready-to-use directory that previously did not exist.
     *
     * @return A {@link Path} pointing towards a folder within the system-designated temporary directory as indicated by
     * the {@literal java.io.tmpdir} system property.
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

    public static void zip(Path targetFile, Path... inputFiles) throws IOException {
        if (inputFiles.length == 0) {
            return;
        }
        // recurse into a single base-directory
        if (inputFiles.length == 1 && Files.isDirectory(inputFiles[0])) {
            final Path[] inputs;
            try (Stream<Path> search = Files.find(inputFiles[0], Integer.MAX_VALUE, (p, a) -> true)) {
                inputs = search.toArray(Path[]::new);
            } catch (IOException inner) {
                throw inner;
            }
            zip(targetFile, inputs);
            return;
        }
        // the root to relativize paths against for the name-resolution of the Zip entries
        final Path root = Files.isDirectory(inputFiles[0]) ? inputFiles[0] : inputFiles[0].getParent();
        // ensure parent directory exists
        Files.createDirectories(targetFile.getParent());

        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(targetFile,
            StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING))) {
            for (Path externalFile : inputFiles) {
                // skip root
                if (externalFile.equals(root)
                    // skip the target file if it's within the resolved input files
                    || externalFile.endsWith(targetFile) || targetFile.endsWith(externalFile)) {
                    continue;
                }
                // only copy the content of regular files
                if (!Files.isRegularFile(externalFile)) {
                    continue;
                }
                final Path relative = root.relativize(externalFile);
                ZipEntry entry = new ZipEntry(relative.toString());
                zos.putNextEntry(entry);
                Files.copy(externalFile, zos);
                zos.closeEntry();
            }
            zos.finish();
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
}
