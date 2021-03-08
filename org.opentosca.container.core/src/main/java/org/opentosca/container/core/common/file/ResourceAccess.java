package org.opentosca.container.core.common.file;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ResourceAccess {

    private static final Set<URI> knownJarfiles = new HashSet<>();

    private final Path path;

    /**
     * Allows accessing resources retrieved from the classpath through a classloader as {@link Path Paths} with all the
     * benefits that entails. Note that {@link Path} instances returned from {@link #resolvedPath()} are not necessarily
     * compatible to the old {@link java.io.File} API.
     *
     * @param resource The URL of a resource returned by the classloader.
     * @throws IOException              If a filesystem needed to be constructed for proper access to the resource and
     *                                  construction or retrieval of a cached instance failed.
     * @throws IllegalArgumentException If the URL could not be converted to a URI
     */
    public ResourceAccess(URL resource) throws IOException, IllegalArgumentException {
        if (resource.getProtocol().startsWith("jar")) {
            // split resolved jar-URL into jarfile and entry path
            String[] parts = resource.toString().split("!");
            assert (parts.length == 2);

            final URI jarURI = URI.create(parts[0]);
            // abusing add returning false if the element already exists
            FileSystem resourceFileSystem = knownJarfiles.add(jarURI)
                ? FileSystems.newFileSystem(jarURI, Collections.emptyMap())
                : FileSystems.getFileSystem(jarURI);

            path = resourceFileSystem.getPath(parts[1]);
        } else {
            path = Paths.get(URI.create(resource.toString()));
        }
    }

    public static Path resolveUrl(URL resource) throws IOException, IllegalArgumentException {
        return new ResourceAccess(resource).resolvedPath();
    }

    public static String readResourceAsString(URL resource) throws IOException, IllegalArgumentException {
        final Path resolved = resolveUrl(resource);
        return new String(Files.readAllBytes(resolved));
    }

    public Path resolvedPath() {
        return path;
    }
}
