package org.opentosca.container.api.controller.content;

import java.net.URI;
import java.util.Objects;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.opentosca.container.api.dto.ResourceSupport;
import org.opentosca.container.api.legacy.resources.utilities.Utilities;
import org.opentosca.container.api.util.UriUtils;
import org.opentosca.container.core.model.AbstractDirectory;
import org.opentosca.container.core.model.AbstractFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.ApiOperation;

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
  @ApiOperation(value = "Get links", response = ResourceSupport.class)
  public Response getLinks(@Context final UriInfo uriInfo) {
    ResourceSupport dto = new ResourceSupport();
    for (final AbstractDirectory directory : this.directory.getDirectories()) {
      URI uri = UriUtils.encode(uriInfo.getAbsolutePathBuilder().path(directory.getName()).build());
      dto.add(Link.fromUri(uri).rel(directory.getName()).build());
    }
    for (final AbstractFile file : this.directory.getFiles()) {
      URI uri = UriUtils.encode(uriInfo.getAbsolutePathBuilder().path(file.getName()).build());
      dto.add(Link.fromUri(uri).rel(file.getName()).build());
    }
    dto.add(Link.fromUri(UriUtils.encode(uriInfo.getAbsolutePath())).rel("self").build());
    return Response.ok(dto).build();
  }

  @Path("/{path}")
  public Object getPath(@PathParam("path") String path, @Context final UriInfo uriInfo) {
    path = Utilities.URLencode(path);
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
    logger.warn("Path '{}' does not exist in directory '{}'", path, directory.getPath());
    return Response.status(Status.NOT_FOUND).build();
  }
}
