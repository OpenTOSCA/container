package org.opentosca.container.api.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.Status;
import javax.xml.namespace.QName;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import org.eclipse.winery.model.selfservice.Application;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.repository.backend.filebased.FileUtils;
import org.opentosca.container.api.controller.content.DirectoryController;
import org.opentosca.container.api.dto.CsarDTO;
import org.opentosca.container.api.dto.CsarListDTO;
import org.opentosca.container.api.dto.request.CsarTransformRequest;
import org.opentosca.container.api.dto.request.CsarUploadRequest;
import org.opentosca.container.api.service.CsarService;
import org.opentosca.container.api.util.ModelUtil;
import org.opentosca.container.connector.winery.WineryConnector;
import org.opentosca.container.control.OpenToscaControlService;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.common.uri.UriUtil;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.model.csar.backwards.FileSystemDirectory;
import org.opentosca.container.core.service.CsarStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.RestController;

@Api
@javax.ws.rs.Path("/csars")
@RestController
//@RequestMapping("/csars")
public class CsarController {

  private static Logger logger = LoggerFactory.getLogger(CsarController.class);

  @Context
  private UriInfo uriInfo;

  // used to generate plans, but not for storage anymore
  @Inject
  private CsarService csarService;

  @Inject
  private CsarStorageService storage;

  @Inject
  private OpenToscaControlService controlService;

  @GET
  @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @ApiOperation(value = "Get all CSARs", response = CsarListDTO.class)
  public Response getCsars() {
    try {
      final CsarListDTO list = new CsarListDTO();
      for (final Csar csarContent : this.storage.findAll()) {
        final String id = csarContent.id().csarName();
        final CsarDTO csar = new CsarDTO();
        csar.setId(id);
        csar.setDescription(csarContent.description());
        csar.add(Link.fromUri(this.uriInfo.getBaseUriBuilder().path(CsarController.class)
          .path(CsarController.class, "getCsar").build(id))
          .rel("self").build());
        list.add(csar);
      }
      list.add(Link.fromResource(CsarController.class).rel("self").baseUri(this.uriInfo.getBaseUri()).build());
      return Response.ok(list).build();
    } catch (Exception e) {
      logger.warn("Exception when fetching all CSARs:", e);
      throw new ServerErrorException(Response.serverError().build());
    }
  }

  @GET
  @javax.ws.rs.Path("/{csar}")
  @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @ApiOperation(value = "Get a CSAR", response = CsarDTO.class)
  public Response getCsar(@ApiParam("ID of CSAR") @PathParam("csar") final String id) {
    try {
      final Csar csarContent = storage.findById(new CsarId(id));
      final Application metadata = csarContent.selfserviceMetadata();

      final CsarDTO csar = CsarDTO.Converter.convert(metadata);
      // Absolute URLs for icon and image
      final String urlTemplate = "{0}csars/{1}/content/servicetemplates/{2}/{3}/SELFSERVICE-Metadata/{4}";

      TServiceTemplate entryServiceTemplate = csarContent.entryServiceTemplate();
      // double encoding, otherwise the link breaks
      final String namespaceSegment = UriUtil.encodePathSegment(UriUtil.encodePathSegment(entryServiceTemplate.getTargetNamespace()));
      final String nameSegment = UriUtil.encodePathSegment(UriUtil.encodePathSegment(entryServiceTemplate.getName()));
      final String baseUri = this.uriInfo.getBaseUri().toString();
      if (csar.getIconUrl() != null) {
        final String iconUrl =
          MessageFormat.format(urlTemplate, baseUri, id, namespaceSegment, nameSegment, csar.getIconUrl());
        csar.setIconUrl(iconUrl);
      }
      if (csar.getImageUrl() != null) {
        final String imageUrl =
          MessageFormat.format(urlTemplate, baseUri, id, namespaceSegment, nameSegment, csar.getImageUrl());
        csar.setImageUrl(imageUrl);
      }

      csar.setId(id);
      if (csar.getName() == null) {
        csar.setName(id);
      }
      csar.add(Link.fromResource(ServiceTemplateController.class).rel("servicetemplates")
        .baseUri(this.uriInfo.getBaseUri()).build(id));

      csar.add(Link.fromUri(this.uriInfo.getBaseUriBuilder().path(CsarController.class)
        .path(CsarController.class, "getContent").build(id))
        .rel("content").baseUri(this.uriInfo.getBaseUri()).build(id));
      csar.add(Link.fromUri(this.uriInfo.getBaseUriBuilder().path(CsarController.class)
        .path(CsarController.class, "getCsar").build(id))
        .rel("self").build());

      csar.add(Link.fromUri(this.uriInfo.getBaseUriBuilder().path(ServiceTemplateController.class)
        .path(ServiceTemplateController.class, "getServiceTemplate")
        .build(id, UriUtil.encodePathSegment(entryServiceTemplate.getId())))
        .rel("servicetemplate").baseUri(this.uriInfo.getBaseUri()).build());

      return Response.ok(csar).build();
    } catch (NoSuchElementException e) {
      return Response.status(Status.NOT_FOUND).build();
    }
  }

  @javax.ws.rs.Path("/{csar}/content")
  @ApiOperation(hidden = true, value = "")
  public DirectoryController getContent(@PathParam("csar") final String id) {
    try {
      return new DirectoryController(new FileSystemDirectory(storage.findById(new CsarId(id)).getSaveLocation()));
    } catch (NoSuchElementException e) {
      throw new javax.ws.rs.NotFoundException(e);
    }
  }

  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @ApiOperation(hidden = true, value = "")
  public Response uploadCsar(MultipartFormDataInput input) {
    InputPart formInput = input.getFormDataMap().get("file").get(0);
    MultivaluedMap<String, String> headers = formInput.getHeaders();
    String fileName = extractFileName(headers);
    if (fileName == null) {
      logger.info("Could not extract fileName from form input");
      return Response.status(Status.BAD_REQUEST).build();
    }
    InputStream is = null;
    try {
      is = formInput.getBody(InputStream.class, null);
      if (is == null) {
        logger.warn("Could not read formInput as InputStream");
        return Response.status(Status.BAD_REQUEST).build();
      }

      logger.info("Uploading new CSAR file \"{}\"", fileName);
      return handleCsarUpload(fileName, is);
    } catch (IOException e) {
      return Response.serverError().entity(e).build();
    } finally {
      if (is != null) {
        try {
          is.close();
        } catch (IOException e) {
          throw new UncheckedIOException(e);
        }
      }
    }
  }

  private String extractFileName(MultivaluedMap<String, String> headers) {
    String[] contentDispostion = headers.getFirst("Content-Disposition").split("\\s*;\\s*");
    for (String kvPair : contentDispostion) {
      if (kvPair.startsWith("filename")) {
        String[] name = kvPair.split("=");
        String quoted = name[1].trim();
        // drops the surrounding quotes
        return quoted.substring(1, quoted.length() - 1);
      }
    }
    return null;
  }

  @POST
  @Consumes( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @ApiOperation(value = "Handles an upload request for a CSAR file")
  public Response uploadCsar(@ApiParam(required = true) final CsarUploadRequest request) {

    if (request == null) {
      return Response.status(Status.BAD_REQUEST).build();
    }

    logger.info("Uploading new CSAR based on request payload: name={}; url={}", request.getName(),
      request.getUrl());

    String filename = request.getName();
    if (!filename.endsWith(".csar")) {
      filename = filename + ".csar";
    }

    try {
      final URL url = new URL(request.getUrl());

      return handleCsarUpload(filename, url.openStream());
    } catch (final Exception e) {
      logger.error("Error uploading CSAR: {}", e.getMessage(), e);
      return Response.serverError().build();
    }
  }

  private Response handleCsarUpload(final String filename, final InputStream is) {

    Path tempFile = storage.storeCSARTemporarily(filename, is);
    if (tempFile == null) {
      // writing to temporary file failed
      return Response.serverError().build();
    }
    CsarId csarId = null;
    try {
      csarId = storage.storeCSAR(tempFile);
    } catch (UserException e) {
      FileUtils.forceDelete(tempFile);
      return Response.notAcceptable(null).entity(e).build();
    } catch (SystemException e) {
      FileUtils.forceDelete(tempFile);
      return Response.serverError().entity(e).build();
    }

    // FIXME I'm pretty sure that invoking ToscaProcessing is unnecessary with the new model
    Csar storedCsar = storage.findById(csarId);
    try {
      if (!this.csarService.generatePlans(storedCsar)) {
        logger.warn("Planning the CSAR failed");
        return Response.serverError().build();
      }
    } catch (Exception e) {
      logger.warn("Planning the CSAR [{}] failed with an exception", csarId.csarName(), e);
      try {
        this.storage.deleteCSAR(csarId);
      } catch (Exception log) {
        logger.warn("Failed to delete CSAR [{}] with failed plans on import", csarId.csarName());
      }
      return Response.serverError().build();
    }

    // FIXME maybe this only makes sense when we have generated plans :/
    this.controlService.declareStored(csarId);

    final List<TServiceTemplate> serviceTemplates = storedCsar.serviceTemplates();
    for (final TServiceTemplate serviceTemplate : serviceTemplates) {
      logger.trace("Invoke plan deployment for service template \"{}\" of CSAR \"{}\"", serviceTemplate.getName(), csarId.csarName());
      if (!this.controlService.invokePlanDeployment(csarId, serviceTemplate)) {
        logger.info("Error deploying plan for service template \"{}\" of CSAR \"{}\"", serviceTemplate.getName(), csarId.csarName());
        // FIXME do a rollback!
        return Response.serverError().build();
      }
    }

    // TODO this is such a brutal hack, won't go through reviews....
    final WineryConnector wc = new WineryConnector();
    boolean repoAvailable = wc.isWineryRepositoryAvailable();

    final StringBuilder strB = new StringBuilder();
    // quick and dirty parallel thread to upload the csar to the container repository
    // This is needed for the state save feature
    Thread parallelUploadThread = new Thread(() -> {
      if (wc.isWineryRepositoryAvailable()) {
        try {
          strB.append(wc.uploadCSAR(tempFile.toFile(), false));
        }  catch (IOException | URISyntaxException e) {
          e.printStackTrace();
        }
      }
    });

    if (repoAvailable) {
      parallelUploadThread.start();
    }

    try {
      if (ModelUtil.hasOpenRequirements(storedCsar)) {
        if (repoAvailable) {
          while (parallelUploadThread.isAlive()) {
            // wait till the upload is finished
          }
          this.controlService.deleteCsar(csarId);
          return Response.status(Response.Status.NOT_ACCEPTABLE)
            .entity("{ \"Location\": \"" + wc.getServiceTemplateURI(QName.valueOf(strB.toString())).toString() + "\" }")
            .build();
        } else {
          logger.error("CSAR has open requirements but Winery repository is not available");
          try {
            this.storage.deleteCSAR(csarId);
          } catch (Exception log) {
            logger.warn("Failed to delete CSAR [{}] with open requirements on import", csarId.csarName());
          }
          return Response.serverError().build();
        }
      }
    }
    catch (final Exception e) {
      logger.error("Error resolving open requirements: {}", e.getMessage(), e);
      return Response.serverError().build();
    }

    logger.info("Uploading and storing CSAR \"{}\" was successful", csarId.csarName());
    final URI uri =
      UriUtil.encode(this.uriInfo.getAbsolutePathBuilder().path(CsarController.class, "getCsar").build(csarId.csarName()));
    return Response.created(uri).build();
  }

  @DELETE
  @javax.ws.rs.Path("/{csar}")
  @ApiOperation(value = "Delete a CSAR")
  public Response deleteCsar(@ApiParam("ID of CSAR") @PathParam("csar") final String id) {
    Csar csarContent;
    try {
      csarContent = storage.findById(new CsarId(id));
    } catch (NoSuchElementException e) {
      return Response.notModified().build();
    }

    logger.info("Deleting CSAR \"{}\"", id);
    final List<String> errors = this.controlService.deleteCsar(csarContent.id());

    if (errors.size() > 0) {
      logger.error("Error deleting CSAR");
      errors.forEach(s -> logger.error(s));
      return Response.serverError().build();
    }
    return Response.noContent().build();
  }

  @POST
  @javax.ws.rs.Path("/transform")
  @ApiOperation(value = "Transform this CSAR to a new CSAR")
  @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  public Response transformCsar(@ApiParam(required = true) final CsarTransformRequest request) {
    CsarId csarId = this.csarService.generateTransformationPlans(new CsarId(request.getSourceCsarName()), new CsarId(request.getTargetCsarName()));
    this.controlService.declareStored(csarId);

    boolean success = this.controlService.invokeToscaProcessing(csarId);
    if (success) {
      Csar storedCsar = storage.findById(csarId);
      final List<TServiceTemplate> serviceTemplates = storedCsar.serviceTemplates();
      for (final TServiceTemplate serviceTemplate : serviceTemplates) {
        logger.trace("Invoke plan deployment for service template \"{}\" of CSAR \"{}\"", serviceTemplate.getName(), csarId.csarName());
        if (!this.controlService.invokePlanDeployment(csarId, serviceTemplate)) {
          logger.info("Error deploying plan for service template \"{}\" of CSAR \"{}\"", serviceTemplate.getName(), csarId.csarName());
          success = false;
        }
      }
    }

    return success
      ? Response.ok().build()
      : Response.serverError().build();
  }
}
