package org.opentosca.container.api.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
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
import org.opentosca.bus.management.utils.MBUtils;
import org.opentosca.container.api.service.CsarService;
import org.opentosca.container.api.service.InstanceService;
import org.opentosca.container.api.service.NodeTemplateService;
import org.opentosca.container.api.service.ServiceTemplateService;
import org.opentosca.container.api.util.UriUtil;
import org.opentosca.container.core.engine.IToscaEngineService;
import org.opentosca.container.core.model.csar.CSARContent;
import org.opentosca.placement.CapablePlacementNode;
import org.opentosca.placement.PlacementMatch;
import org.opentosca.placement.PlacementService;
import org.opentosca.placement.ToBePlacedNode;
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
	private NodeTemplateService nodeTemplateService;
	private IToscaEngineService toscaEngineService;
	private CapablePlacementNode capablePlacementNode;
	private ToBePlacedNode toBePlacedNode;

	public PlacementController(final PlacementService placementService, final CsarService csarService,
			final ServiceTemplateService serviceTemplateService, NodeTemplateService nodeTemplateService,
			final InstanceService instanceService, final IToscaEngineService toscaEngineService) {
		this.placementService = placementService != null ? placementService : new PlacementService();
		this.toscaEngineService = toscaEngineService;
		this.instanceService = instanceService;
		this.serviceTemplateService = serviceTemplateService;
		this.nodeTemplateService = nodeTemplateService;
		this.csarService = csarService;
		this.capablePlacementNode = new CapablePlacementNode();
		this.toBePlacedNode = new ToBePlacedNode();
	}

	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_JSON })
	// TODO: value = what gets returned to the REST caller
	@ApiOperation(hidden = true, value = "")
	public Response startPlacement(final Request request) {
		
		List<CapablePlacementNode> listOfCapablePlacementNodes = new ArrayList<CapablePlacementNode>();
		List<ToBePlacedNode> listOfToBePlacedNodes = new ArrayList<ToBePlacedNode>();

		if (request == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}

		/**
		 * The CSAR under this resource path that wants to be placed.
		 * At this point we are certain that this CSAR has open requirements,
		 * since the UI only allows such CSARS to reach this resource.
		 */
		final CSARContent csarContent = this.csarService.findById(csarId);

		if (!this.csarService.hasServiceTemplate(csarContent.getCSARID(), serviceTemplateId)) {
			logger.info("Service template \"" + serviceTemplateId + "\" could not be found");
			throw new NotFoundException("Service template \"" + serviceTemplateId + "\" could not be found");
		} else {
			logger.info("Service template \"" + serviceTemplateId + "\" was found. Placement possibilities are being explored.");
		}

		/**
		 * Goes Through all CSARs and their ServiceTemplates and their NodeTemplates to
		 * identify the nodes with the necessary capabilities and puts them in a 
		 * CapablePlacementNodeContext Object which contains everything the PlacementService 
		 * needs to perform the actual placement operation.
		 */
		this.csarService.findAll().forEach((CSARContent csar) -> {
			Set<String> serviceTemplates = this.serviceTemplateService
					.getServiceTemplatesOfCsar(csar.getCSARID().toString());
			serviceTemplates.forEach(serviceTemplate -> {
				logger.info("Service Template found: " + serviceTemplate);

				List<String> nodeTemplates = this.toscaEngineService.getNodeTemplatesOfServiceTemplate(csar.getCSARID(),
						QName.valueOf(serviceTemplate));		
				
				// For each nodeTemplate, check if it is an OS node
				nodeTemplates.forEach(nodeTemplate -> {
					String osNode = MBUtils.getOperatingSystemNodeTemplateID(csar.getCSARID(),
							QName.valueOf(serviceTemplate), nodeTemplate);
					// Search the OS Nodes for Capabilities
					if (osNode != null) {
						// Set id of OS Node to context object
						this.capablePlacementNode.setOsNode(osNode);
						// Get NodeType of the OS node
						String nodeType = this.nodeTemplateService.getNodeTemplateById(csar.getCSARID().toString(),
								QName.valueOf(serviceTemplate), nodeTemplate).getNodeType();
						logger.info("NodeType of OSNode: " + nodeType);
						

						List<QName> nodeCaps = this.toscaEngineService.getNodeTemplateCapabilities(csar.getCSARID(),
								QName.valueOf(serviceTemplate), nodeTemplate);
						if (!nodeCaps.isEmpty()) {
							// Add Capabilities
							this.capablePlacementNode.setCapsOfOSNode(nodeCaps);
						}
						
						/**
						 * Find instances of OS Nodes and add their idiosyncrasies to the capablePlacementNode Object
						 */
						this.instanceService.getServiceTemplateInstances(serviceTemplate).forEach(instance -> {
							Long instanceIDOfServiceTemplate = instance.getId();
							
							logger.info("Instance of Service Template found");
							instance.getNodeTemplateInstances().forEach(nodeTemplateInstance -> {
								
								// If instance is OS Node
								if (nodeTemplateInstance.getName().equals(nodeTemplate)) {
									logger.info("instance name equals node template name!!!");
									Map<String, String> propertyMap = nodeTemplateInstance.getPropertiesAsMap();
									Long instanceIDOfOSNode = nodeTemplateInstance.getId();
									
									// Add propertyMap to OS Node
									this.capablePlacementNode.setPropertyMap(propertyMap);
									this.capablePlacementNode.setInstanceIDOfOSNode(instanceIDOfOSNode);
									this.capablePlacementNode.setInstanceIDOfServiceTemplateOfOsNode(instanceIDOfServiceTemplate);
									propertyMap.forEach((key, value) -> {
										logger.info("property -> key: " + key + "         value: " + value);
									});
									
									listOfCapablePlacementNodes.add(this.capablePlacementNode);
								}
							});
						});
					}
				});
			});
		});
		
		/**
		 * The part where the CSAR-to-be-placed gets prepared for the Placement algorithm
		 */
		this.nodeTemplateService.getNodeTemplatesOfServiceTemplate(csarId, serviceTemplateId).forEach(nodeTemplate -> {
			List<QName> nodeReqs = this.toscaEngineService.getNodeTemplateRequirements(csarContent.getCSARID(),
					QName.valueOf(serviceTemplateId), nodeTemplate.getName());
			
			String nodeTypeOfToBePlacedNode = this.nodeTemplateService.getNodeTemplateById(csarId,
					QName.valueOf(serviceTemplateId), nodeTemplate.getName()).getNodeType();
					
			if (!nodeReqs.isEmpty()) {
				nodeReqs.forEach(req -> {
					logger.info("Requirement of to-be-placed nodeTemplate: " + req.toString());
				});
				this.toBePlacedNode.setCsarIdOfToBePlacedNode(csarId);
				this.toBePlacedNode.setServiceTemplateOfToBePlacedNode(serviceTemplateId);
				this.toBePlacedNode.setToBePlacedNode(nodeTemplate.getName());
				this.toBePlacedNode.setReqsOfToBePlacedNode(nodeReqs);
				this.toBePlacedNode.setNodeTypeOfToBePlacedNode(nodeTypeOfToBePlacedNode);
				
				// Add it to the list
				listOfToBePlacedNodes.add(this.toBePlacedNode);
				
			} else {
				logger.info("NodeTemplate: " + nodeTemplate.getName() + " has no requirements!");
			}
		});
		
		logger.info("List of to be placed nodes: ----------------------");
		listOfToBePlacedNodes.forEach(tbpNode -> {
			logger.info(tbpNode.getToBePlacedNode());
		});
		logger.info("--------------------------------------------------\n");
		
		logger.info("List of capable nodes: ---------------------------");
		listOfCapablePlacementNodes.forEach(cpbNode -> {
			logger.info(cpbNode.getOsNode());
		});
		logger.info("--------------------------------------------------\n");
		
		
		logger.info("Trying to find Placement Candidates...");

		List<PlacementMatch> foundMatches = this.placementService.findPlacementCandidate(listOfCapablePlacementNodes, listOfToBePlacedNodes);

		
		final URI uri = UriUtil.generateSubResourceURI(this.uriInfo, csarId, false);

		return Response.ok(uri).build();
		// Response.ok(uri).build();
		/*
		 * } catch (final NullPointerException e) { logger.error(e.toString()); return
		 * Response.ok("Blub").build(); }
		 */
	}
}
