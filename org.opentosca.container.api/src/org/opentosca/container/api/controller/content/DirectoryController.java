package org.opentosca.container.api.controller.content;

import java.util.Objects;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.opentosca.container.api.dto.ResourceSupport;
import org.opentosca.container.api.util.UriUtil;
import org.opentosca.container.core.model.AbstractDirectory;
import org.opentosca.container.core.model.AbstractFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


@Api
public class DirectoryController {

    private static Logger logger = LoggerFactory.getLogger(DirectoryController.class);

    private final AbstractDirectory directory;


    public DirectoryController(final AbstractDirectory directory) {
        Objects.nonNull(directory);
        this.directory = directory;
        logger.info("Directory path: {}", directory.getPath());
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Gets links to all files and directories contained in this CSAR",
                  response = ResourceSupport.class)
    public Response getLinks(@Context final UriInfo uriInfo) {
        final ResourceSupport dto = new ResourceSupport();
        for (final AbstractDirectory directory : this.directory.getDirectories()) {
            dto.add(UriUtil.generateSubResourceLink(uriInfo, directory.getName(), false, directory.getName()));
        }
        for (final AbstractFile file : this.directory.getFiles()) {
            dto.add(UriUtil.generateSubResourceLink(uriInfo, file.getName(), false, file.getName()));
        }
        dto.add(UriUtil.generateSelfLink(uriInfo));
        return Response.ok(dto).build();
    }


    @Path("/{path}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Gets the sub-file or sub-directory specified")
    public Object getPath(@PathParam("path") String path, @Context final UriInfo uriInfo) {
        path = UriUtil.encodePathSegment(path);
        logger.debug("Serve path '{}' of directory '{}'", path, this.directory.getPath());
        for (final AbstractDirectory directory : this.directory.getDirectories()) {
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
