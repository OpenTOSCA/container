package org.opentosca.container.api.controller.content;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.opentosca.container.api.dto.ResourceSupport;
import org.opentosca.container.core.common.uri.UriUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DirectoryController {

    private static Logger logger = LoggerFactory.getLogger(DirectoryController.class);


    private final java.nio.file.Path dirPath;

    public DirectoryController(java.nio.file.Path directory) {
        Objects.nonNull(directory);
        this.dirPath = directory;
        logger.debug("Directory path: {}", directory.toString());
    }

    @GET
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getLinks(@Context final UriInfo uriInfo, @QueryParam("recursive") String recursive) throws IOException {
        logger.debug("Get links for directory controller on directory: {} with recursive flag: {}", this.dirPath.toString(), recursive);
        final ResourceSupport dto = new ResourceSupport();
        if (recursive == null) {

            Collection<java.nio.file.Path> subfolder = this.getDirectories();

            Collection<java.nio.file.Path> files = this.getFiles();

            for (final java.nio.file.Path directory : subfolder) {
                logger.debug("Found sub directory: {}", directory.getFileName().toString());
                dto.add(UriUtil.generateSubResourceLink(uriInfo, directory.getFileName().toString(), true, directory.getFileName().toString()));
            }
            for (final java.nio.file.Path file : files) {
                dto.add(UriUtil.generateSubResourceLink(uriInfo, file.getFileName().toString(), true, file.getFileName().toString()));
            }
        } else {
            Collection<java.nio.file.Path> files = this.getFilesRecursively();
            for (final java.nio.file.Path file : files) {
                dto.add(UriUtil.generateSubResourceLink(uriInfo, file.getFileName().toString(), true, file.getFileName().toString()));
            }
        }

        dto.add(UriUtil.generateSelfLink(uriInfo));
        return Response.ok(dto).build();
    }

    @Path("/{path}")
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Object getPath(@PathParam("path") String path, @Context final UriInfo uriInfo) throws IOException {
        logger.debug("Serve path '{}' of directory '{}'", path, this.dirPath.toString());
        for (final java.nio.file.Path directory : this.getDirectories()) {
            logger.debug("Found directory: {}", directory.getFileName());
            if (directory.getFileName().toString().equals(path)) {
                logger.debug("Path '{}' is a directory...", path);
                return new DirectoryController(directory);
            }
        }
        for (final java.nio.file.Path file : this.getFiles()) {
            if (file.getFileName().toString().equals(path)) {
                logger.debug("Path '{}' is a file...", path);
                return new FileController(file);
            }
        }
        logger.warn("Path '{}' does not exist in directory '{}'", path, this.dirPath.toString());

        throw new NotFoundException(
            String.format("Path '%s' does not exist in directory '%s'", path, this.dirPath.toString()));
    }

    private Collection<java.nio.file.Path> getDirectories() throws IOException {
        List<java.nio.file.Path> subfolder = Files.walk(this.dirPath, 1)
            .filter(Files::isDirectory)
            .collect(Collectors.toList());
        subfolder.remove(0);
        return subfolder;
    }

    private Collection<java.nio.file.Path> getFiles() throws IOException {
        Collection<java.nio.file.Path> files = Files.walk(this.dirPath, 1)
            .filter(Files::isRegularFile)
            .collect(Collectors.toList());
        return files;
    }

    private Collection<java.nio.file.Path> getFilesRecursively() throws IOException {
        List<java.nio.file.Path> files = Files.walk(this.dirPath)
            .filter(Files::isRegularFile)
            .collect(Collectors.toList());
        return files;
    }
}
