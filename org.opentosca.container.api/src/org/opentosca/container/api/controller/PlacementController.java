package org.opentosca.container.api.controller;

import java.net.URI;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;
import javax.xml.namespace.QName;

import org.opentosca.container.api.dto.RelationshipTemplateDTO;
import org.opentosca.container.api.dto.RelationshipTemplateListDTO;
import org.opentosca.container.api.dto.request.CreateServiceTemplateInstanceRequest;
import org.opentosca.container.api.service.CsarService;
import org.opentosca.container.api.service.InstanceService;
import org.opentosca.container.api.service.PlacementService;
import org.opentosca.container.api.service.RelationshipTemplateService;
import org.opentosca.container.api.service.ServiceTemplateService;
import org.opentosca.container.api.util.UriUtil;
import org.opentosca.container.core.model.csar.CSARContent;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.next.model.DeploymentTest;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.repository.ServiceTemplateInstanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api
public class PlacementController {

    private static final Logger logger = LoggerFactory.getLogger(PlacementController.class);
    
    @ApiParam("ID of CSAR")
    @PathParam("csar")
    String csarId;

    @ApiParam("qualified name of the service template")
    @PathParam("servicetemplate")
    String serviceTemplateId;

    @Context
    private UriInfo uriInfo;

    @Context
    ResourceContext resourceContext;

    private PlacementService placementService;
    private InstanceService instanceService;
    private CsarService csarService;
    private ServiceTemplateService serviceTemplateService;

    public PlacementController(final PlacementService placementService,
    						   final CsarService csarService,
    						   final ServiceTemplateService serviceTemplateService,
                               final InstanceService instanceService) {
        this.placementService = placementService;
        this.instanceService = instanceService;
    	this.serviceTemplateService = serviceTemplateService;
    	this.csarService = csarService;
    }
    
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    @ApiOperation(hidden = true, value = "")
    public Response startPlacement(final Request request) {

        if (request == null) {
        	logger.info("PLACEMENT::Request was null");
            return Response.status(Status.BAD_REQUEST).build();
        }
        
		// final CSARID csar = this.serviceTemplateService.checkServiceTemplateExistence(csarId, serviceTemplateId);
		
		final CSARContent csarContent = this.csarService.findById(csarId);
		
        if (!this.csarService.hasServiceTemplate(csarContent.getCSARID(), serviceTemplateId)) {
            logger.info("Service template \"" + serviceTemplateId + "\" could not be found");
            throw new NotFoundException("Service template \"" + serviceTemplateId + "\" could not be found");
        } else {
        	logger.info("Service template \"" + serviceTemplateId + "\" was found!");
        }
        
        logger.info("Inside PlacementService::findPlacementCandidates(csarId, serviceTempladeId)");
		logger.info("csarId: " + csarId);
		logger.info("serviceTemplateId: " + serviceTemplateId);

        try {
        	this.setPlacementService(new PlacementService());
        	
        	// final PlacementCandidates foundCandidates = this.placementService.findPlacementCandidates(this.csarId, this.serviceTemplateId);
        	String str = this.placementService.findPlacementCandidates(this.csarId, this.serviceTemplateId);
        	logger.info("Trying to find Placement Candidates...");

            final URI uri = UriUtil.generateSubResourceURI(this.uriInfo, csarId, false);

            return Response.ok(uri).build();
            		//Response.ok(uri).build();
        }
        catch (final NullPointerException e) {
        	logger.error(e.toString());
            return Response.ok("Blub").build();
        }
    }

    public void setPlacementService(PlacementService placementService) {
        this.placementService = placementService;
    }

	public void setInstanceService(InstanceService instanceService) {
		this.instanceService = instanceService;
	}

	public void setServiceTemplateService(ServiceTemplateService serviceTemplateService) {
		this.serviceTemplateService = serviceTemplateService;
	}
}
