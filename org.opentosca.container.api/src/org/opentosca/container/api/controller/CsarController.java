package org.opentosca.container.api.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
import org.opentosca.container.api.dto.request.CsarTransformRequest;
import org.opentosca.container.api.dto.request.CsarUploadRequest;
import org.opentosca.container.api.service.CsarService;
import org.opentosca.container.api.util.ModelUtil;
import org.opentosca.container.api.util.UriUtil;
import org.opentosca.container.connector.winery.WineryConnector;
import org.opentosca.container.control.IOpenToscaControlService;
import org.opentosca.container.core.common.EntityExistsException;
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

@Api
@Path("/csars")
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
    @ApiOperation(value = "Get all CSARs", response = CsarListDTO.class)
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
    @ApiOperation(value = "Get a CSAR", response = CsarDTO.class)
    public Response getCsar(@ApiParam("ID of CSAR") @PathParam("csar") final String id) {

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
        // Add direct link to service template
        final Set<String> serviceTemplates = this.csarService.getServiceTemplates(new CSARID(id));
        if (serviceTemplates.size() == 1) {
            final String name = serviceTemplates.stream().findFirst().get();
            csar.add(Link.fromUri(this.uriInfo.getBaseUriBuilder().path(ServiceTemplateController.class)
                                              .path(ServiceTemplateController.class, "getServiceTemplate")
                                              .build(id, UriUtil.encodePathSegment(name)))
                         .rel("servicetemplate").baseUri(this.uriInfo.getBaseUri()).build());
        }
        csar.add(Link.fromUri(this.uriInfo.getBaseUriBuilder().path(CsarController.class)
                                          .path(CsarController.class, "getContent").build(id))
                     .rel("content").baseUri(this.uriInfo.getBaseUri()).build(id));
        csar.add(Link.fromUri(this.uriInfo.getBaseUriBuilder().path(CsarController.class)
                                          .path(CsarController.class, "getCsar").build(id))
                     .rel("self").build());

        return Response.ok(csar).build();
    }

    @Path("/{csar}/content")
    @ApiOperation(hidden = true, value = "")
    public DirectoryController getContent(@PathParam("csar") final String id) {
        final CSARContent csarContent = this.csarService.findById(id);
        return new DirectoryController(csarContent.getCsarRoot());
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(hidden = true, value = "")
    public Response uploadCsar(@FormDataParam(value = "enrichment") final String applyEnrichment,
                               @FormDataParam(value = "file") final InputStream is,
                               @FormDataParam("file") final FormDataContentDisposition file) {

        if (is == null || file == null) {
            return Response.status(Status.BAD_REQUEST).build();
        }

        logger.info("Uploading new CSAR file \"{}\", size {}", file.getFileName(), file.getSize());

        try {
            return handleCsarUpload(file.getFileName(), is, applyEnrichment);
        }
        catch (SystemException e) {
            e.printStackTrace();
            return Response.serverError().entity(e).build();
        }
        catch (UserException e) {

            e.printStackTrace();
            return Response.serverError().entity(e).build();
        }

    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Handles an upload request for a CSAR file")
    public Response uploadCsar(@ApiParam(required = true) final CsarUploadRequest request) {

        if (request == null) {
            return Response.status(Status.BAD_REQUEST).build();
        }


        logger.info("Uploading new CSAR based on request payload: name={}; url={}; applyEnrichment={}",
                    request.getName(), request.getUrl(), request.getEnrich());

        String filename = request.getName();
        if (!filename.endsWith(".csar")) {
            filename = filename + ".csar";
        }

        try {
            final URL url = new URL(request.getUrl());

            return handleCsarUpload(filename, url.openStream(), request.getEnrich());
        }
        catch (final Exception e) {
            logger.error("Error uploading CSAR: {}", e.getMessage(), e);
            return Response.serverError().build();
        }
    }


    private Response handleCsarUpload(final String filename, final InputStream is,
                                      final String applyEnrichment) throws SystemException, UserException {

        final File file = this.csarService.storeTemporaryFile(filename, is);

        final WineryConnector wc = new WineryConnector();

        if (Objects.nonNull(applyEnrichment) && Boolean.parseBoolean(applyEnrichment)) {
            logger.debug("Enrichment status is true. Continue with enrichment.");
            wc.performManagementFeatureEnrichment(file);
        } else {
            logger.debug("Enrichment status is null or false. Continue without enrichment.");
        }

        CSARID csarId;

        try {
            csarId = this.fileService.storeCSAR(file.toPath());
        }
        catch (final EntityExistsException e) {
            logger.error("Failed to store CSAR: {}", e.getMessage(), e);
            return Response.status(Status.CONFLICT).build();
        }
        catch (final Exception e) {
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
                logger.info("Invoke plan deployment for service template \"{}\" of CSAR \"{}\"", serviceTemplate,
                            csarId.getFileName());
                if (!this.controlService.invokePlanDeployment(csarId, serviceTemplate)) {
                    logger.error("Error deploying plan for service template \"{}\" of CSAR \"{}\"", serviceTemplate,
                                 csarId.getFileName());
                    success = false;
                }
            }
        }

        // TODO this is such a brutal hack, won't go through reviews....
        final boolean repoAvailable = wc.isWineryRepositoryAvailable();
        final StringBuilder strB = new StringBuilder();

        // quick and dirty parallel thread to upload the csar to the container
        // repository
        // This is needed for the state save feature
        final Thread parallelUploadThread = new Thread(() -> {
            if (wc.isWineryRepositoryAvailable()) {
                try {
                    strB.append(wc.uploadCSAR(file, false));
                }
                catch (final URISyntaxException e1) {
                    e1.printStackTrace();
                }
                catch (final IOException e2) {
                    e2.printStackTrace();
                }
            }
        });

        if (repoAvailable) {
            parallelUploadThread.start();
        }

        try {
            if (ModelUtil.hasOpenRequirements(csarId, this.engineService)) {

                if (repoAvailable) {
                    while (parallelUploadThread.isAlive()) {
                        // wait till the upload is finished
                    }
                    this.controlService.deleteCSAR(csarId);
                    return Response.status(Response.Status.NOT_ACCEPTABLE)
                                   .entity("{ \"Location\": \""
                                       + wc.getServiceTemplateURI(QName.valueOf(strB.toString())).toString() + "\" }")
                                   .build();
                } else {
                    logger.error("CSAR has open requirments but Winery repository is not available");
                    try {
                        this.fileService.deleteCSAR(csarId);
                    }
                    catch (final Exception e) {
                        // Ignore
                        logger.error("Error deleting csar after open requirements check: {}", e.getMessage(), e);
                    }
                    return Response.serverError().build();
                }
            }
        }
        catch (final Exception e) {
            logger.error("Error resolving open requirements: {}", e.getMessage(), e);
            return Response.serverError().build();
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
    @ApiOperation(value = "Delete a CSAR")
    public Response deleteCsar(@ApiParam("ID of CSAR") @PathParam("csar") final String id) {
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

    @POST
    @Path("/transform")
    @ApiOperation(value = "Transform this CSAR to a new CSAR")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response transformCsar(@ApiParam(required = true) final CsarTransformRequest request) {

        final String sourceCsarName = request.getSourceCsarName();
        final String targetCsarName = request.getTargetCsarName();

        final CSARID csarId =
            this.csarService.generateTransformationPlans(new CSARID(sourceCsarName), new CSARID(targetCsarName));

        this.controlService.setDeploymentProcessStateStored(csarId);
        boolean success = this.controlService.invokeTOSCAProcessing(csarId);


        if (success) {
            final List<QName> serviceTemplates =
                this.engineService.getToscaReferenceMapper().getServiceTemplateIDsContainedInCSAR(csarId);
            for (final QName serviceTemplate : serviceTemplates) {
                logger.info("Invoke plan deployment for service template \"{}\" of CSAR \"{}\"", serviceTemplate,
                            csarId.getFileName());
                if (!this.controlService.invokePlanDeployment(csarId, serviceTemplate)) {
                    logger.error("Error deploying plan for service template \"{}\" of CSAR \"{}\"", serviceTemplate,
                                 csarId.getFileName());
                    success = false;
                }
            }
        }

        if (success) {
            return Response.ok().build();
        } else {
            return Response.serverError().build();
        }
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
