package org.opentosca.container.api.controller;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.opentosca.container.api.dto.PlanDTO;
import org.opentosca.container.api.dto.ResourceSupport;
import org.opentosca.container.api.dto.boundarydefinitions.InterfaceDTO;
import org.opentosca.container.api.dto.boundarydefinitions.InterfaceListDTO;
import org.opentosca.container.api.dto.boundarydefinitions.OperationDTO;
import org.opentosca.container.api.dto.boundarydefinitions.PropertiesDTO;
import org.opentosca.container.api.service.CsarService;
import org.opentosca.container.api.util.UriUtils;
import org.opentosca.container.core.engine.IToscaEngineService;
import org.opentosca.container.core.engine.IToscaReferenceMapper;
import org.opentosca.container.core.model.csar.CSARContent;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.tosca.extension.PlanTypes;
import org.opentosca.container.core.tosca.model.TExportedInterface;
import org.opentosca.container.core.tosca.model.TExportedOperation;
import org.opentosca.container.core.tosca.model.TPlan;
import org.opentosca.container.core.tosca.model.TPropertyMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Path("/csars/{csar}/servicetemplates/{servicetemplate}/boundarydefinitions")
@Api("/csars/{csar}/servicetemplates/{servicetemplate}/boundarydefinitions")
public class BoundaryDefinitionController {

	private final Logger logger = LoggerFactory.getLogger(CsarController.class);
	
	@Context
	private UriInfo uriInfo;

	@Context
	private Request request;
	
	private CsarService csarService;
	
	@SuppressWarnings("unused")
	private IToscaEngineService engineService;

	private IToscaReferenceMapper referenceMapper;


	@GET
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@ApiOperation(value = "Get Boundary Definitions for CSAR", response = ResourceSupport.class, responseContainer = "List")
	public Response getBoundaryDefinitions(@PathParam("csar") final String csar,
			@PathParam("servicetemplate") final String servicetemplate) {
		
		final CSARContent csarContent = this.csarService.findById(csar);
		if (!this.csarService.hasServiceTemplate(csarContent.getCSARID(), servicetemplate)) {
			this.logger.info("Service template \"" + servicetemplate + "\" could not be found");
			throw new NotFoundException("Service template \"" + servicetemplate + "\" could not be found");
		}
		
		final ResourceSupport links = new ResourceSupport();
		links.add(Link.fromUri(UriUtils.encode(this.uriInfo.getAbsolutePathBuilder().path("properties").build())).rel("properties").build());
		links.add(Link.fromUri(UriUtils.encode(this.uriInfo.getAbsolutePathBuilder().path("interfaces").build())).rel("interfaces").build());
		// TODO This resource seems to be unused and not implemented
		// links.add(Link.fromUri(UriUtils.encode(this.uriInfo.getAbsolutePathBuilder().path("propertyconstraints").build())).rel("propertyconstraints").build());
		// TODO This resource seems to be unused and not implemented
		// links.add(Link.fromUri(UriUtils.encode(this.uriInfo.getAbsolutePathBuilder().path("requirements").build())).rel("requirements").build());
		// TODO This resource seems to be unused and not implemented
		// links.add(Link.fromUri(UriUtils.encode(this.uriInfo.getAbsolutePathBuilder().path("capabilities").build())).rel("capabilities").build());
		// TODO: This resource seems to be unused and not implemented
		// links.add(Link.fromUri(UriUtils.encode(this.uriInfo.getAbsolutePathBuilder().path("policies").build())).rel("policies").build());
		links.add(Link.fromUri(UriUtils.encode(this.uriInfo.getAbsolutePath())).rel("self").build());

		return Response.ok(links).build();
	}

	@GET
	@Path("/properties")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@ApiOperation(value = "Get Properties of the CSAR file", response = PropertiesDTO.class, responseContainer = "List")
	public Response getProperties(@PathParam("csar") final String csar,
			@PathParam("servicetemplate") final String servicetemplate) {
		
		final CSARContent csarContent = this.csarService.findById(csar);
		if (!this.csarService.hasServiceTemplate(csarContent.getCSARID(), servicetemplate)) {
			this.logger.info("Service template \"" + servicetemplate + "\" could not be found");
			throw new NotFoundException("Service template \"" + servicetemplate + "\" could not be found");
		}
		
		final String xmlFragment =
				this.referenceMapper.getServiceTemplateBoundsPropertiesContent(csarContent.getCSARID(), QName.valueOf(servicetemplate));
		final List<TPropertyMapping> propertyMappings =
				this.referenceMapper.getPropertyMappings(csarContent.getCSARID(), QName.valueOf(servicetemplate));

		final PropertiesDTO dto = new PropertiesDTO();
		this.logger.debug("XML Fragement: {}", xmlFragment);
		dto.setXmlFragment(xmlFragment);
		if (propertyMappings != null) {
			this.logger.debug("Found <{}> property mappings", propertyMappings.size());
			dto.setPropertyMappings(propertyMappings);
		}
		dto.add(Link.fromUri(UriUtils.encode(this.uriInfo.getAbsolutePath())).rel("self").build());

		return Response.ok(dto).build();
	}

	@GET
	@Path("/interfaces")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@ApiOperation(value = "Get interfaces of the CSAR file", response = InterfaceDTO.class, responseContainer = "List")
	public Response getInterfaces(@PathParam("csar") final String csar,
			@PathParam("servicetemplate") final String servicetemplate) {
		
		final CSARContent csarContent = this.csarService.findById(csar);
		if (!this.csarService.hasServiceTemplate(csarContent.getCSARID(), servicetemplate)) {
			this.logger.info("Service template \"" + servicetemplate + "\" could not be found");
			throw new NotFoundException("Service template \"" + servicetemplate + "\" could not be found");
		}
		
		final List<String> interfaces =
				this.referenceMapper.getBoundaryInterfacesOfServiceTemplate(csarContent.getCSARID(), QName.valueOf(servicetemplate));
		this.logger.debug("Found <{}> interface(s) in Service Template \"{}\" of CSAR \"{}\" ", interfaces.size(), servicetemplate, csar);

		final InterfaceListDTO list = new InterfaceListDTO();
		list.add(interfaces.stream().map(name -> {
			final InterfaceDTO dto = new InterfaceDTO();
			dto.setName(name);
			dto.add(Link.fromUri(UriUtils.encode(this.uriInfo.getAbsolutePathBuilder().path(name).build())).rel("self").build());
			return dto;
		}).collect(Collectors.toList()).toArray(new InterfaceDTO[] {}));
		list.add(Link.fromUri(UriUtils.encode(this.uriInfo.getAbsolutePath())).rel("self").build());
		
		return Response.ok(list).build();
	}

	@GET
	@Path("/interfaces/{name}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response getInterface(@PathParam("name") final String name, @PathParam("csar") final String csar,
			@PathParam("servicetemplate") final String servicetemplate) {
		
		final CSARContent csarContent = this.csarService.findById(csar);
		if (!this.csarService.hasServiceTemplate(csarContent.getCSARID(), servicetemplate)) {
			this.logger.info("Service template \"" + servicetemplate + "\" could not be found");
			throw new NotFoundException("Service template \"" + servicetemplate + "\" could not be found");
		}
		
		final List<TExportedOperation> operations =
				this.getExportedOperations(csarContent.getCSARID(), QName.valueOf(servicetemplate), name);
		this.logger.debug("Found <{}> operation(s) for Interface \"{}\" in Service Template \"{}\" of CSAR \"{}\" ", operations.size(), name, servicetemplate, csar);
		
		final Map<String, OperationDTO> ops = operations.stream().map(o -> {
			
			final OperationDTO op = new OperationDTO();
			
			op.setName(o.getName());
			op.setNodeOperation(o.getNodeOperation());
			op.setRelationshipOperation(o.getRelationshipOperation());
			
			if (o.getPlan() != null) {
				final PlanDTO plan = new PlanDTO((TPlan) o.getPlan().getPlanRef());
				op.setPlan(plan);
				
				// Compute the according URL for the Build or Management Plan
				final URI planUrl;
				if (PlanTypes.BUILD.toString().equals(plan.getPlanType())) {
					// If it's a build plan
					planUrl =
							this.uriInfo.getBaseUriBuilder().path("/csars/{csar}/servicetemplates/{servicetemplate}/buildplans/{buildplan}").build(csar, servicetemplate, plan.getId());
				}
				else {
					// ... else we assume it's a management plan
					planUrl =
							this.uriInfo.getBaseUriBuilder().path("/csars/{csar}/servicetemplates/{servicetemplate}/instances/:id/managementplans/{managementplan}").build(csar, servicetemplate, plan.getId());
				}
				plan.add(Link.fromUri(UriUtils.encode(planUrl)).rel("self").build());
				op.add(Link.fromUri(UriUtils.encode(planUrl)).rel("plan").build());
			}
			
			return op;
		}).collect(Collectors.toMap(OperationDTO::getName, t -> t));
		
		final InterfaceDTO dto = new InterfaceDTO();
		dto.setName(name);
		dto.setOperations(ops);
		dto.add(Link.fromUri(UriUtils.encode(this.uriInfo.getAbsolutePath())).rel("self").build());
		
		return Response.ok(dto).build();
	}
	
	private List<TExportedOperation> getExportedOperations(final CSARID csarId, final QName serviceTemplate,
			final String interfaceName) {
		final Map<QName, List<TExportedInterface>> exportedInterfacesOfCsar =
				this.referenceMapper.getExportedInterfacesOfCSAR(csarId);
		final List<TExportedInterface> exportedInterfaces = exportedInterfacesOfCsar.get(serviceTemplate);
		for (final TExportedInterface exportedInterface : exportedInterfaces) {
			if (exportedInterface.getName().equalsIgnoreCase(interfaceName)) { return exportedInterface.getOperation(); }
		}
		return null;
	}
	
	public void setCsarService(final CsarService csarService) {
		this.csarService = csarService;
	}
	
	public void setEngineService(final IToscaEngineService engineService) {
		this.engineService = engineService;
		// We cannot inject an instance of {@link IToscaReferenceMapper} since
		// it is manually created in our default implementation of {@link
		// IToscaEngineService}
		this.referenceMapper = engineService.getToscaReferenceMapper();
	}
}
