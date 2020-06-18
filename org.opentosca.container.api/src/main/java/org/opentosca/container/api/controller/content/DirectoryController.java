package org.opentosca.container.api.controller.content;

import java.util.Objects;

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
import org.opentosca.container.core.model.AbstractDirectory;
import org.opentosca.container.core.model.AbstractFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DirectoryController {

    private static Logger logger = LoggerFactory.getLogger(DirectoryController.class);

    private final AbstractDirectory directory;

    public DirectoryController(final AbstractDirectory directory) {
        Objects.nonNull(directory);
        this.directory = directory;
        logger.debug("Directory path: {}", directory.getPath());
    }

    @GET
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getLinks(@Context final UriInfo uriInfo, @QueryParam("recursive") String recursive) {
        logger.debug("Get links for directory controller on directory: {} with recursive flag: {}", directory.getPath(), recursive);
        final ResourceSupport dto = new ResourceSupport();
        if (recursive == null) {
            for (final AbstractDirectory directory : this.directory.getDirectories()) {
                logger.debug("Found sub directory: {}", directory.getName());
                dto.add(UriUtil.generateSubResourceLink(uriInfo, directory.getName(), true, directory.getName()));
            }
            for (final AbstractFile file : this.directory.getFiles()) {
                dto.add(UriUtil.generateSubResourceLink(uriInfo, file.getName(), true, file.getName()));
            }
        } else {
            for (final AbstractFile file : this.directory.getFilesRecursively()) {
                dto.add(UriUtil.generateSubResourceLink(uriInfo, file.getPath(), true, file.getName()));
            }
        }

        dto.add(UriUtil.generateSelfLink(uriInfo));
        return Response.ok(dto).build();
    }

    @Path("/{path}")
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Object getPath(@PathParam("path") String path, @Context final UriInfo uriInfo) {
        logger.debug("Serve path '{}' of directory '{}'", path, this.directory.getPath());
        for (final AbstractDirectory directory : this.directory.getDirectories()) {
            logger.debug("Found directory: {}", directory.getName());
            if (directory.getName().equals(path)) {
                logger.debug("Path '{}' is a directory...", path);
                return new DirectoryController(directory);
            }
        }
        for (final AbstractFile file : this.directory.getFiles()) {
            if (file.getName().equals(path)) {
                logger.debug("Path '{}' is a file...", path);
                return new FileController(file);
            }
        }
        logger.warn("Path '{}' does not exist in directory '{}'", path, this.directory.getPath());

        throw new NotFoundException(
            String.format("Path '%s' does not exist in directory '%s'", path, this.directory.getPath()));
    }
}
