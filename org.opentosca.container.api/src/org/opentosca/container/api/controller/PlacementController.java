package org.opentosca.container.api.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
import org.opentosca.container.core.engine.impl.ToscaReferenceMapper;
import org.opentosca.container.core.model.csar.CSARContent;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.next.model.NodeTemplateInstanceProperty;
import org.opentosca.container.core.next.model.Property;
import org.opentosca.container.core.next.xml.PropertyParser;
import org.opentosca.container.core.tosca.model.TEntityTemplate;
import org.opentosca.container.core.tosca.model.TNodeTemplate;
import org.opentosca.container.core.tosca.model.TRequirement;
import org.opentosca.container.core.tosca.model.TServiceTemplate;
import org.opentosca.placement.CapablePlacementNode;
import org.opentosca.placement.PlacementCandidate;
import org.opentosca.placement.PlacementMatch;
import org.opentosca.placement.PlacementService;
import org.opentosca.placement.ToBePlacedNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api
public class PlacementController {

	private static final Logger logger = LoggerFactory.getLogger(PlacementController.class);
	public static ToscaReferenceMapper toscaReferenceMapper = null;

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

	public PlacementController(final PlacementService placementService, final CsarService csarService,
			final ServiceTemplateService serviceTemplateService, NodeTemplateService nodeTemplateService,
			final InstanceService instanceService, final IToscaEngineService toscaEngineService) {
		this.placementService = placementService != null ? placementService : new PlacementService();
		this.toscaEngineService = toscaEngineService;
		this.instanceService = instanceService;
		this.serviceTemplateService = serviceTemplateService;
		this.nodeTemplateService = nodeTemplateService;
		this.csarService = csarService;
		toscaReferenceMapper = new ToscaReferenceMapper();
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
		 * The CSAR under this resource path that wants to be placed. At this point we
		 * are certain that this CSAR has open requirements, since the UI only allows
		 * such CSARS to reach this resource.
		 */
		final CSARContent csarContent = this.csarService.findById(csarId);

		if (!this.csarService.hasServiceTemplate(csarContent.getCSARID(), serviceTemplateId)) {
			logger.info("Service template \"" + serviceTemplateId + "\" could not be found");
			throw new NotFoundException("Service template \"" + serviceTemplateId + "\" could not be found");
		} else {
			logger.info("Service template \"" + serviceTemplateId
					+ "\" was found. Placement possibilities are being explored.");
		}

		/**
		 * Goes Through all CSARs and their ServiceTemplates and their NodeTemplates to
		 * identify the nodes with the necessary capabilities and puts them in a
		 * CapablePlacementNodeContext Object which contains everything the
		 * PlacementService needs to perform the actual placement operation.
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
						// Get NodeType of the OS node
						String nodeType = this.nodeTemplateService.getNodeTemplateById(csar.getCSARID().toString(),
								QName.valueOf(serviceTemplate), nodeTemplate).getNodeType();

						List<QName> nodeCaps = this.toscaEngineService.getNodeTemplateCapabilities(csar.getCSARID(),
								QName.valueOf(serviceTemplate), nodeTemplate);
						
						

						/**
						 * Find instances of OS Nodes and add their idiosyncrasies to the
						 * capablePlacementNode Object
						 */
						this.instanceService.getServiceTemplateInstances(serviceTemplate).forEach(instance -> {
							// capablePlacementNode.setInstanceIDOfServiceTemplateOfOsNode(instance.getId());

							logger.info(
									"+++ ServiceTemplate: " + serviceTemplate + " --- Instance: " + instance.getId());

							instance.getNodeTemplateInstances().forEach(nodeTemplateInstance -> {

								logger.info("nodeTemplateInstance: " + nodeTemplateInstance.getName() + " "
										+ nodeTemplateInstance.getId());

								// capablePlacementNode.setInstanceIDOfOSNode(nodeTemplateInstance.getId());

								if (nodeTemplateInstance.getName().equals(nodeTemplate)) {
									Map<String, String> propertyMap = nodeTemplateInstance.getPropertiesAsMap();

									// Add propertyMap to OS Node
									// capablePlacementNode.setPropertyMap(propertyMap);

//									propertyMap.forEach((key, value) -> {
//										logger.info("property -> key: " + key + "         value: " + value);
//									});
									logger.info("capablePlacemntNodeToBeAdded: " + new CapablePlacementNode(osNode,
											nodeType, serviceTemplate, csar.getCSARID().toString(), nodeCaps,
											nodeTemplateInstance.getId(), instance.getId(), propertyMap)
													.getInstanceIDOfServiceTemplateOfOsNode());
									listOfCapablePlacementNodes.add(new CapablePlacementNode(osNode, nodeType,
											serviceTemplate, csar.getCSARID().toString(), nodeCaps,
											nodeTemplateInstance.getId(), instance.getId(), propertyMap));
								}
							});
						});
					}
				});
			});
		});

		/**
		 * The part where the CSAR-to-be-placed gets prepared for the Placement
		 * algorithm
		 */
		this.nodeTemplateService.getNodeTemplatesOfServiceTemplate(csarId, serviceTemplateId).forEach(nodeTemplate -> {
			List<QName> nodeReqs = this.toscaEngineService.getNodeTemplateRequirements(csarContent.getCSARID(),
					QName.valueOf(serviceTemplateId), nodeTemplate.getId());
			

			String nodeTypeOfToBePlacedNode = this.nodeTemplateService
					.getNodeTemplateById(csarId, QName.valueOf(serviceTemplateId), nodeTemplate.getId())
					.getNodeType();
			
			
			// Properties
	        // We set the properties of the template as initial properties
	        final Document propertiesAsDocument =
	            this.nodeTemplateService.getPropertiesOfNodeTemplate(csarId, QName.valueOf(serviceTemplateId), nodeTemplate.getId());

	        Map<String, String> templatePropertyMap = new HashMap<String, String>();
	        
	        if (propertiesAsDocument != null) {
	        	Property properties;
				try {
					properties = this.instanceService.convertDocumentToProperty(propertiesAsDocument, Property.class);
					final PropertyParser parser = new PropertyParser();
					templatePropertyMap = parser.parse(properties.getValue());
					
					logger.info("properties " + properties.getValue());
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            
	        }

			if (!nodeReqs.isEmpty()) {
				
				nodeReqs.forEach(req -> {
					logger.info("Requirement of to-be-placed nodeTemplate: " + req.toString());
				});

				// Add it to the list
				listOfToBePlacedNodes.add(new ToBePlacedNode(nodeTemplate.getName(), nodeTypeOfToBePlacedNode,
						serviceTemplateId, csarId, nodeReqs, templatePropertyMap));

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

		PlacementCandidate placementCandidate = this.placementService.findPlacementCandidate(listOfCapablePlacementNodes,
				listOfToBePlacedNodes);

		final URI uri = UriUtil.generateSubResourceURI(this.uriInfo, csarId, false);

		return Response.ok(placementCandidate).build();
	}
}
