package org.opentosca.container.api.controller.content;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.io.FilenameUtils;
import org.opentosca.container.api.dto.ResourceSupport;
import org.opentosca.container.core.common.uri.UriUtil;
import org.opentosca.container.core.model.AbstractDirectory;
import org.opentosca.container.core.model.AbstractFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileController {

  private static Logger logger = LoggerFactory.getLogger(FileController.class);
  private static final Map<String, MediaType> IMAGE_TYPES = new HashMap<>();

  static {
    IMAGE_TYPES.put("png", MediaType.valueOf("image/png"));
    IMAGE_TYPES.put("jpg", MediaType.valueOf("image/jpeg"));
    IMAGE_TYPES.put("jpeg", MediaType.valueOf("image/jpeg"));
    IMAGE_TYPES.put("gif", MediaType.valueOf("image/gif"));
  }

  private final AbstractFile file;


  public FileController(final AbstractFile file) {
    Objects.nonNull(file);
    this.file = file;
    logger.debug("File path: {}", file.getPath());
  }
  
  @GET
  @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  public Response getLinks(@Context final UriInfo uriInfo) {
      logger.debug("Get link for file controller on file: {}", file.getPath());
      final ResourceSupport dto = new ResourceSupport();
     

      dto.add(UriUtil.generateSelfLink(uriInfo));
      return Response.ok(dto).build();
  }

  @GET
  @Produces( {MediaType.APPLICATION_OCTET_STREAM, "image/*"})
  public Response getFile() {
    logger.info("Attempt to get file: \"{}\"", this.file.getPath());
    try {
      final InputStream is = this.file.getFileAsInputStream();
      // Image or normal file download?
      final String ext = FilenameUtils.getExtension(this.file.getName());
      final MediaType imageType = IMAGE_TYPES.get(ext);
      if (imageType != null) {
        // Serve the image
        return Response.ok(is, imageType).build();
      } else {
        // ... download the file
        return Response.ok(is)
          .header("Content-Disposition", "attachment; filename=\"" + this.file.getName() + "\"")
          .build();
      }
    } catch (final Exception e) {
      logger.error("Could not get file: {}", e.getMessage(), e);
      return Response.serverError().build();
    }
  }
}
