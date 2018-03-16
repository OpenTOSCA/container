package org.opentosca.container.api.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.selfservice.Application;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.opentosca.container.api.controller.content.DirectoryController;
import org.opentosca.container.api.dto.CsarDTO;
import org.opentosca.container.api.dto.CsarListDTO;
import org.opentosca.container.api.dto.request.CsarUploadRequest;
import org.opentosca.container.api.service.CsarService;
import org.opentosca.container.api.util.ModelUtil;
import org.opentosca.container.api.util.UriUtil;
import org.opentosca.container.connector.winery.WineryConnector;
import org.opentosca.container.control.IOpenToscaControlService;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.engine.IToscaEngineService;
import org.opentosca.container.core.model.csar.CSARContent;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.service.ICoreFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;

@Path("/csars")
@Api(value = "/")
public class CsarController {

    private static Logger logger = LoggerFactory.getLogger(CsarController.class);

    @Context
    private UriInfo uriInfo;

    private CsarService csarService;

    private ICoreFileService fileService;

    private IToscaEngineService engineService;

    private IOpenToscaControlService controlService;

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Gets all CSARs", response = CsarDTO.class, responseContainer = "List")
    public Response getCsars() {

        final CsarListDTO list = new CsarListDTO();

        for (final CSARContent csarContent : this.csarService.findAll()) {
            final String id = csarContent.getCSARID().getFileName();
            final CsarDTO csar = new CsarDTO();
            csar.setId(id);
            csar.setDescription(csarContent.getCSARDescription());
            csar.add(Link.fromUri(this.uriInfo.getBaseUriBuilder().path(CsarController.class)
                                              .path(CsarController.class, "getCsar").build(id))
                         .rel("self").build());
            list.add(csar);
        }

        list.add(Link.fromResource(CsarController.class).rel("self").baseUri(this.uriInfo.getBaseUri()).build());

        return Response.ok(list).build();
    }

    @GET

    @Path("/{csar}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Gets a given CSAR", response = CsarDTO.class)
    public Response getCsar(@ApiParam("CSAR id") @PathParam("csar") final String id) {

        final CSARContent csarContent = this.csarService.findById(id);
        final Application metadata = this.csarService.getSelfserviceMetadata(csarContent);

        final CsarDTO csar = CsarDTO.Converter.convert(metadata);

        // Absolute URLs for icon and image
        final String urlTemplate = "{0}csars/{1}/content/SELFSERVICE-Metadata/{2}";

        if (csar.getIconUrl() != null) {
            final String iconUrl =
                MessageFormat.format(urlTemplate, this.uriInfo.getBaseUri().toString(), id, csar.getIconUrl());
            csar.setIconUrl(iconUrl);
        }
        if (csar.getImageUrl() != null) {
            final String imageUrl =
                MessageFormat.format(urlTemplate, this.uriInfo.getBaseUri().toString(), id, csar.getImageUrl());
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

        return Response.ok(csar).build();
    }


    @Path("/{csar}/content")
    public DirectoryController getContent(@ApiParam("CSAR id") @PathParam("csar") final String id) {
        final CSARContent csarContent = this.csarService.findById(id);
        return new DirectoryController(csarContent.getCsarRoot());
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Uploads a CSAR file", notes = "handles missing requirements", response = Response.class,
                  hidden = true)
    @ApiResponses({@ApiResponse(code = 400, message = "Bad Request"),
                   @ApiResponse(code = 406, message = "CSAR is not acceptable"),
                   @ApiResponse(code = 204,
                                message = "Created - The CSAR has been successfully created and its location is returned as the value of the location header",
                                responseHeaders = {@ResponseHeader(name = "location",
                                                                   description = "the URI of the create CSAR")})})
    public Response uploadFile(@ApiParam(value = "The CSAR file",
                                         required = true) @FormDataParam(value = "file") final InputStream is,
                               @ApiParam(hidden = true) @FormDataParam("file") final FormDataContentDisposition file) throws IOException,
                                                                                                                      URISyntaxException,
                                                                                                                      UserException,
                                                                                                                      SystemException {

        if (is == null || file == null) {
            return Response.status(Status.BAD_REQUEST).build();
        }

        logger.info("Uploading new CSAR file \"{}\", size {}", file.getFileName(), file.getSize());
        return handleCsarUpload(file.getFileName(), is);
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Handles an upload request for a CSAR file", notes = "handles missing requirements",
                  response = Response.class)
    @ApiResponses({@ApiResponse(code = 400, message = "Bad Request"),
                   @ApiResponse(code = 406, message = "CSAR is not acceptable"),
                   @ApiResponse(code = 204,
                                message = "Created - The CSAR has been successfully created and its location is returned as the value of the location header",
                                responseHeaders = {@ResponseHeader(name = "location",
                                                                   description = "the URI of the create CSAR")})})
    public Response upload(@ApiParam(required = true, value = "CSAR upload reques.") final CsarUploadRequest request) {

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
        }
        catch (final Exception e) {
            logger.error("Error uploading CSAR: {}", e.getMessage(), e);
            return Response.serverError().build();
        }
    }

    private Response handleCsarUpload(final String filename, final InputStream is) {

        final File file = this.csarService.storeTemporaryFile(filename, is);

        CSARID csarId;

        try {
            csarId = this.fileService.storeCSAR(file.toPath());
        }
        catch (final Exception e) {
            logger.error("Failed to store CSAR: {}", e.getMessage(), e);
            return Response.serverError().build();
        }

        this.controlService.invokeTOSCAProcessing(csarId);

        try {

            if (ModelUtil.hasOpenRequirements(csarId, this.engineService)) {
                final WineryConnector wc = new WineryConnector();
                if (wc.isWineryRepositoryAvailable()) {
                    final QName serviceTemplate = wc.uploadCSAR(file);
                    this.controlService.deleteCSAR(csarId);
                    return Response.status(Response.Status.NOT_ACCEPTABLE).entity("{ \"Location\": \""
                        + wc.getServiceTemplateURI(serviceTemplate).toString() + "\" }").build();
                } else {
                    logger.error("CSAR has open requirments but Winery repository is not available");
                    try {
                        this.fileService.deleteCSAR(csarId);
                    }
                    catch (final Exception e) {
                        // Ignore
                    }
                    return Response.serverError().build();
                }
            }
        }
        catch (final Exception e) {
            logger.error("Error resolving open requirements: {}", e.getMessage(), e);
            return Response.serverError().build();
        }

        this.controlService.deleteCSAR(csarId);
        try {
            csarId = this.fileService.storeCSAR(file.toPath());
        }
        catch (UserException | SystemException e) {
            logger.error("Failed to store CSAR: {}", e.getMessage(), e);
            return Response.serverError().build();
        }

        csarId = this.csarService.generatePlans(csarId);
        if (csarId == null) {
            return Response.serverError().build();
        }

        this.controlService.setDeploymentProcessStateStored(csarId);
        boolean success = this.controlService.invokeTOSCAProcessing(csarId);

        if (success) {
            final List<QName> serviceTemplates =
                this.engineService.getToscaReferenceMapper().getServiceTemplateIDsContainedInCSAR(csarId);
            for (final QName serviceTemplate : serviceTemplates) {
                logger.info("Invoke IA deployment for service template \"{}\" of CSAR \"{}\"", serviceTemplate,
                            csarId.getFileName());
                if (!this.controlService.invokeIADeployment(csarId, serviceTemplate)) {
                    logger.error("Error deploying IA for service template \"{}\" of CSAR \"{}\"", serviceTemplate,
                                 csarId.getFileName());
                    success = false;
                }
                logger.info("Invoke plan deployment for service template \"{}\" of CSAR \"{}\"", serviceTemplate,
                            csarId.getFileName());
                if (!this.controlService.invokePlanDeployment(csarId, serviceTemplate)) {
                    logger.error("Error deploying plan for service template \"{}\" of CSAR \"{}\"", serviceTemplate,
                                 csarId.getFileName());
                    success = false;
                }
            }
        }

        if (!success) {
            return Response.serverError().build();
        }

        logger.info("Uploading and storing CSAR \"{}\" was successful", csarId.getFileName());
        final URI uri =
            UriUtil.encode(this.uriInfo.getAbsolutePathBuilder().path(CsarController.class, "getCsar").build(csarId));
        return Response.created(uri).build();
    }

    @DELETE
    @Path("/{csar}")
    @ApiOperation(value = "Deletes a CSAR file", response = Response.class)
    public Response delete(@ApiParam("CSAR id") @PathParam("csar") final String id) {
        final CSARContent csarContent = this.csarService.findById(id);

        logger.info("Deleting CSAR \"{}\"", id);
        final List<String> errors = this.controlService.deleteCSAR(csarContent.getCSARID());

        if (errors.size() > 0) {
            logger.error("Error deleting CSAR");
            errors.forEach(s -> logger.error(s));
            return Response.serverError().build();
        }

        return Response.noContent().build();
    }

    public void setCsarService(final CsarService csarService) {
        this.csarService = csarService;
    }

    public void setFileService(final ICoreFileService fileService) {
        this.fileService = fileService;
    }

    public void setEngineService(final IToscaEngineService engineService) {
        this.engineService = engineService;
    }

    public void setControlService(final IOpenToscaControlService controlService) {
        this.controlService = controlService;
    }
}
