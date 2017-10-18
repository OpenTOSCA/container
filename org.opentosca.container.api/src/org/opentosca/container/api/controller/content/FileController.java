package org.opentosca.container.api.controller.content;

import java.io.InputStream;
import java.util.Map;
import java.util.Objects;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FilenameUtils;
import org.opentosca.container.core.model.AbstractFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import io.swagger.annotations.ApiOperation;

public class FileController {

  private static Logger logger = LoggerFactory.getLogger(FileController.class);

  private final AbstractFile file;


  private static final Map<String, MediaType> IMAGE_TYPES = Maps.newHashMap();

  static {
    IMAGE_TYPES.put("png", MediaType.valueOf("image/png"));
    IMAGE_TYPES.put("jpg", MediaType.valueOf("image/jpeg"));
    IMAGE_TYPES.put("jpeg", MediaType.valueOf("image/jpeg"));
    IMAGE_TYPES.put("gif", MediaType.valueOf("image/gif"));
  }


  public FileController(final AbstractFile file) {
    Objects.nonNull(file);
    this.file = file;
    logger.info("File path: {}", file.getPath());
  }

  @GET
  @Produces({MediaType.APPLICATION_OCTET_STREAM, "image/*"})
  @ApiOperation(value = "Tries to get file")
  public Response getFile() {
    logger.info("Attempt to get file: \"{}\"", this.file.getPath());
    try {
      final InputStream is = this.file.getFileAsInputStream();
      // Image or normal file download?
      String ext = FilenameUtils.getExtension(this.file.getName());
      MediaType imageType = IMAGE_TYPES.get(ext);
      if (imageType != null) {
        // Serve the image
        return Response.ok(is, imageType).build();
      } else {
        // ... download the file
        return Response.ok(is)
            .header("Content-Disposition", "attachment; filename=\"" + this.file.getName() + "\"")
            .build();
      }
    } catch (Exception e) {
      logger.error("Could not get file: {}", e.getMessage(), e);
      return Response.serverError().build();
    }
  }
}
